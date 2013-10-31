package br.com.infowaypi.sensews.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.infowaypi.ecarebc.associados.Especialidade;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.msr.pessoa.PessoaFisicaInterface;
import br.com.infowaypi.sense.sauderecife.GuiaBuilder;
import br.com.infowaypi.sense.sauderecife.GuiaInvalidaException;
import br.com.infowaypi.sense.sauderecife.ans.CaraterSolicitacao;
import br.com.infowaypi.sense.sauderecife.ans.Guia;
import br.com.infowaypi.sense.sauderecife.ans.SituacaoGuia;
import br.com.infowaypi.sense.sauderecife.ans.TipoGuia;
import br.com.infowaypi.sense.sauderecife.handler.ItemProcedimentosHandler;

public enum SenseManager {

	CONFIRMACAO_CONSULTA {
		@SuppressWarnings("rawtypes")
		@Override
		protected void conversaoEspecifica(GuiaSimples guiaOriginal) {
			conversaoComumConsultas(builder, guiaOriginal);
			builder.setSituacao(SituacaoGuia.CONFIRMADA);
		}

		@SuppressWarnings("rawtypes")
		@Override
		protected void checkPrecondicoes(GuiaSimples guiaOriginal) throws SenseConvercaoException {
			if(!guiaOriginal.isConsultaEletiva()) {
				throw new SenseConvercaoException("A guia que está sendo confirmada não representa " +
						"uma consulta eletiva: id " + guiaOriginal.getIdGuia());
			}
		}
	},
	CANCELAMENTO_CONSULTA {
		@SuppressWarnings("rawtypes")
		@Override
		protected void conversaoEspecifica(GuiaSimples guiaOriginal) {
			conversaoComumConsultas(builder, guiaOriginal);
			builder.setSituacao(SituacaoGuia.CANCELADA);
		}

		@SuppressWarnings("rawtypes")
		@Override
		protected void checkPrecondicoes(GuiaSimples guiaOriginal) throws SenseConvercaoException {
			if(!guiaOriginal.isConsultaEletiva()) {
				throw new SenseConvercaoException("A guia que está sendo cancelada não representa uma consulta eletiva.");
			}
			
			if(!guiaOriginal.getSituacao().getDescricao().equals(SituacaoEnum.CANCELADO.descricao())) {
				throw new SenseConvercaoException("A guia que está sendo cancelada não está na situação que indique cancelamento.");
			}
		}
	},
	CONFIRMACAO_EXAME {
		@SuppressWarnings("rawtypes")
		@Override
		protected void conversaoEspecifica(GuiaSimples guiaOriginal) {
			conversaoComumExames(builder, guiaOriginal);
			
			builder.setSituacao(SituacaoGuia.CONFIRMADA);
			ItemProcedimentosHandler handler = builder.solicitacao().procedimentos();
			
			for (Object obj : guiaOriginal.getProcedimentos()) {
				ProcedimentoInterface p = (ProcedimentoInterface) obj;
				handler.addProcedimento(p.getProcedimentoDaTabelaCBHPM().getCodigo(), p.getValorTotal());
			}
		}

		@SuppressWarnings("rawtypes")
		@Override
		protected void checkPrecondicoes(GuiaSimples guiaOriginal) throws SenseConvercaoException {
			if(!guiaOriginal.isExameEletivo()) {
				throw new SenseConvercaoException("A guia que está sendo cancelada não representa exames eletivos.");
			}
		}
	},
	CANCELAMENTO_EXAME {
		@SuppressWarnings("rawtypes")
		@Override
		protected void conversaoEspecifica(GuiaSimples guiaOriginal) {
			conversaoComumExames(builder, guiaOriginal);
			
			builder.setSituacao(SituacaoGuia.CANCELADA);
			ItemProcedimentosHandler handler = builder.solicitacao().procedimentos();
			
			for (ProcedimentoInterface p : getProcedimentosRemovidos(guiaOriginal)) {
				handler.addProcedimento(p.getProcedimentoDaTabelaCBHPM().getCodigo(), p.getValorTotal());
			}
		}

		@SuppressWarnings("rawtypes")
		@Override
		protected void checkPrecondicoes(GuiaSimples guiaOriginal) throws SenseConvercaoException {
			if(!guiaOriginal.isExameEletivo()) {
				throw new SenseConvercaoException("A guia que está sendo cancelada não representa exames eletivos.");
			}
			
			if(getProcedimentosRemovidos(guiaOriginal).isEmpty()) {
				throw new SenseConvercaoException("Não há procedimentos removidos nesta guia.");
			}
		}
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		private Set<ProcedimentoInterface> getProcedimentosRemovidos(GuiaSimples guiaOriginal) {
			Set<ProcedimentoInterface> procedimentos = guiaOriginal.getProcedimentos();
			
			if(guiaOriginal.getSituacao().getDescricao().equals(SituacaoEnum.CANCELADO.descricao())) {
				Set<ProcedimentoInterface> procedimentosNaoRemovido = new HashSet<ProcedimentoInterface>();
				for(ProcedimentoInterface procedimento : procedimentos){
					if(!procedimento.isSituacaoAtual(SituacaoEnum.REMOVIDO.descricao())){
						procedimentosNaoRemovido.add(procedimento);
					}
				}
				return  procedimentosNaoRemovido;
			}
			
			Set<ProcedimentoInterface> removidos = new HashSet<ProcedimentoInterface>();
			
			for (ProcedimentoInterface procedimento : procedimentos) {
				if(procedimento.getSituacao().getDescricao().equals(SituacaoEnum.REMOVIDO.descricao())) {
					removidos.add(procedimento);
				}
			}
			
			return removidos;
		}
	};
	
	private static Log log = LogFactory.getLog(SenseManager.class);
	private Map<String, String> erros;
	protected GuiaBuilder builder;
	
	@SuppressWarnings("rawtypes")
	public void analisar(GuiaSimples guiaOriginal) {
		Guia guiaSense = null;
		
		try {
			guiaSense = this.converter(guiaOriginal);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(guiaSense != null) {
			try {
				enviar(guiaSense);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			System.out.println("Erros de validação ocorridos na conversão de guia para o Sense:");
			
			for (Entry<String, String> erro : erros.entrySet()) {
				System.out.println(erro.getKey() + ": " + erro.getValue());
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	protected abstract void conversaoEspecifica(GuiaSimples guiaOriginal);
	
	@SuppressWarnings("rawtypes")
	protected abstract void checkPrecondicoes(GuiaSimples guiaOriginal) throws SenseConvercaoException;
	
	@SuppressWarnings("rawtypes")
	private Guia converter(GuiaSimples guiaOriginal) throws SenseConvercaoException {
		reset();
		checkPrecondicoes(guiaOriginal);
		
		builder.start()
			.setDataEmissao(guiaOriginal.getDataMarcacao())
			.setNumero(guiaOriginal.getIdGuia().toString())			
			.operadora()
				.setCnpj("123456/SaudeRecife")
				.setFantasia("Saúde Recife")
				.returnToParent()
			.plano()
				.setNome("Saúde Recife")
				.returnToParent()
			.solicitacao()
				.setCarater(CaraterSolicitacao.ELETIVO);
		
		if(guiaOriginal.getPrestador() != null) {
			builder.solicitacao()
				.prestador()
					.setNome(guiaOriginal.getPrestador().getPessoaJuridica().getFantasia())
					.setCnpj(guiaOriginal.getPrestador().getPessoaJuridica().getCnpj());
		}
		
		this.conversaoEspecifica(guiaOriginal);
			
		return  build(builder);
	}

	private Guia build(GuiaBuilder builder) {
		Guia retorno = null;
		
		try {
			builder.setNomeTipoEvento("SolicitacaoExameConsulta");
			retorno = builder.build();
		} catch (GuiaInvalidaException e) {
			log.error("A guia gerada a partir da conversão não passou na validação. Verifique os erros para mais detalhes.");
			
			this.erros = e.getErros();
		}
		
		return retorno;
	}

	private void reset() {
		this.erros = new HashMap<String, String>();
		this.builder = new GuiaBuilder();
	}
	
	private static void enviar(final Guia guia) throws Exception {
		log.info("SENSE - Início do envio de uma nova guia.");
		
		ExecutorService executor = Executors.newFixedThreadPool(1);
		
		executor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					AnaliseEventoManager.getInstance().analisar(guia);
					log.info("SENSE - Guia enviada com sucesso.");
				} catch (Exception e) {
					log.info("SENSE - Falha no envio da guia.");
					e.printStackTrace();
				}
			}
		});
		
		executor.shutdown();
	}
	
	@SuppressWarnings("rawtypes")
	private static void conversaoComumExames(GuiaBuilder builder, GuiaSimples guiaOriginal) {
		builder.setTipo(TipoGuia.SPSADT);
		
		if(guiaOriginal.getGuiaOrigem() != null) {
			GuiaSimples guiaOrigem  = guiaOriginal.getGuiaOrigem();
			
			Especialidade especialidade = guiaOrigem.getEspecialidade();
			
			if(especialidade != null) {
				builder.cbos()
					.setCodigo(especialidade.getIdEspecialidade().toString())
					.setNome(especialidade.getDescricao());
			}
			
			Profissional profissional = guiaOrigem.getProfissional();
			
			if(profissional != null) {
				builder.solicitacao()
					.profissional()
						.setEmail(getEmail(profissional.getPessoaFisica()))
						.setCelular(getCelular(profissional.getPessoaFisica()))
						.setNome(profissional.getPessoaFisica().getNome())
					.conselho()
						.setNumero(profissional.getCrm())
						.setSigla(profissional.getConselho())
						.setUF("PE");
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	private static void conversaoComumConsultas(GuiaBuilder builder, GuiaSimples guiaOriginal) {
		TabelaCBHPM procedimento = (TabelaCBHPM) guiaOriginal.getProcedimentosDaTabelaCBHPM().get(0);
		
		builder
			.setTipo(TipoGuia.CONSULTA)
			.cbos()
				.setCodigo(guiaOriginal.getEspecialidade().getIdEspecialidade().toString())
				.setNome(guiaOriginal.getEspecialidade().getDescricao())
				.returnToParent()
			.solicitacao()
				.procedimentos()
					.addProcedimento(procedimento.getCodigo(), guiaOriginal.getValorTotal())
					.returnToParent()
				.profissional()
					.setEmail(getEmail(guiaOriginal.getProfissional().getPessoaFisica()))
					.setCelular(getCelular(guiaOriginal.getProfissional().getPessoaFisica()))
					.setNome(guiaOriginal.getProfissional().getPessoaFisica().getNome())
					.conselho()
						.setNumero(guiaOriginal.getProfissional().getCrm())
						.setSigla(guiaOriginal.getProfissional().getConselho())
						.setUF("PE");
	}
	
	private static String getEmail(PessoaFisicaInterface pf) {
		return pf.getEmail() != null ? pf.getEmail() : "teste@infoway-pi.com.br";
	}
	
	private static String getCelular(PessoaFisicaInterface pf) {
		return pf.getCelular() != null ? pf.getCelular() : "99999999";
	}
}
