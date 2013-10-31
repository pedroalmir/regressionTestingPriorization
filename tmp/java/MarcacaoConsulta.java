package br.com.infowaypi.ecare.services.consultas;

import java.util.Collection;

import br.com.infowaypi.ecare.enums.MensagemErroEnumSR;
import br.com.infowaypi.ecare.segurados.ResumoSegurados;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.services.BuscarSegurados;
import br.com.infowaypi.ecare.services.Service;
import br.com.infowaypi.ecare.utils.ProfissionalUtils;
import br.com.infowaypi.ecarebc.associados.Especialidade;
import br.com.infowaypi.ecarebc.associados.ManagerPrestador;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsulta;
import br.com.infowaypi.ecarebc.atendimentos.enums.TipoConsultaEnum;
import br.com.infowaypi.ecarebc.atendimentos.validators.ConsultaPeriodicidadeValidator;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.ecarebc.service.consultas.MarcacaoConsultaService;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

@SuppressWarnings("rawtypes")
public class MarcacaoConsulta extends MarcacaoConsultaService<Segurado> {
	
	public ResumoSegurados buscarSegurado(String numeroDoCartao, String cpfDoTitular) throws Exception {
		ResumoSegurados resumo = BuscarSegurados.buscar(numeroDoCartao, cpfDoTitular,Segurado.class);
		resumo.inicializaConsultasPromocionaisLiberadas();
		return resumo;
	}
	
	public GuiaConsulta criarGuiaPrestador(ResumoSegurados resumo, Segurado segurado, Prestador prestador, UsuarioInterface usuario,
			Profissional profissional, Especialidade especialidade, String dataDeAtendimento) throws Exception {
		
		Assert.isNotNull(segurado, MensagemErroEnum.SEGURADO_NAO_INFORMADO.getMessage());

		if (segurado.getDataAdesao() == null){
			throw new RuntimeException(MensagemErroEnumSR.SEGURADO_SEM_DATA_DE_ADESAO.getMessage());
		}

		if (segurado.getSituacao().getDescricao().equals(SituacaoEnum.SUSPENSO)
				&& segurado.getSituacao().getMotivo().equals("Não recadastrado")) {
			throw new ValidateException(MensagemErroEnumSR.SEGURADO_NAO_RECADASTRADO.getMessage());
		}

		validaLimiteDeIdadePorEspecialidade(segurado, especialidade);
		
		GuiaConsulta guiaConsulta = super.criarGuiaPrestador(segurado, TipoConsultaEnum.NORMAL,	prestador, usuario, profissional, especialidade,dataDeAtendimento);
		
		aplicarAcordoConsultaEspecialidade(prestador, especialidade, guiaConsulta);
		/*necessario porque quando a guia é criada ela possui o procedimento default de consulta para clinica medica.
		 * Chamando a validação novamente podemos comparar realmente o procedimento correto da guia.*/
		new ConsultaPeriodicidadeValidator<GuiaConsulta>().templateValidator(guiaConsulta);		

		return guiaConsulta;
	}

	/**
	 * @param prestador
	 * @param especialidade
	 * @param guiaConsulta
	 * @throws Exception
	 */
	private void aplicarAcordoConsultaEspecialidade(Prestador prestador, Especialidade especialidade, GuiaConsulta guiaConsulta) throws Exception {
		TabelaCBHPM tabela = ManagerPrestador.getConsultaEspecialidade(prestador, especialidade, false);   

		if (tabela  !=  null ){
			guiaConsulta.getProcedimentos().clear();
			guiaConsulta.recalcularValores();
			Procedimento procedimento = new Procedimento();   
			procedimento.getSituacao().setDescricao(SituacaoEnum.AGENDADA.descricao());
			procedimento.setProcedimentoDaTabelaCBHPM(tabela);
			procedimento.setGuia(guiaConsulta);
			procedimento.calcularCampos();
			guiaConsulta.addProcedimento(procedimento);
		}
	}
	
	@Override
	public GuiaConsulta criarGuiaMarcador(Segurado segurado,TipoConsultaEnum tipoConsulta,
		UsuarioInterface usuario) throws Exception {
		Assert.isNotNull(segurado, MensagemErroEnum.SEGURADO_NAO_INFORMADO.getMessage());
		return super.criarGuiaMarcador(segurado,tipoConsulta,usuario);
	}
	
	@Override
	public GuiaConsulta criarGuiaLancamento(Segurado segurado, TipoConsultaEnum tipoConsulta,String dataDeAtendimento, Prestador prestador, 
		Profissional profissional, Especialidade especialidade, UsuarioInterface usuario) throws Exception {
		
		Assert.isNotNull(segurado, MensagemErroEnum.SEGURADO_NAO_INFORMADO.getMessage());
		
		validaLimiteDeIdadePorEspecialidade(segurado, especialidade);
		
		GuiaConsulta guiaConsulta = super.criarGuiaLancamento(segurado, tipoConsulta, dataDeAtendimento, prestador, profissional, especialidade, usuario);
		
		aplicarAcordoConsultaEspecialidade(prestador, especialidade, guiaConsulta);
		
		return guiaConsulta;
	}
	
	public GuiaConsulta criarGuiaLancamento(Segurado segurado, Boolean ignorarValidacao, 
		String dataDeAtendimento, Prestador prestador, 
		Profissional profissional, Especialidade especialidade, 
		UsuarioInterface usuario) throws Exception {
		Assert.isNotNull(segurado, MensagemErroEnum.SEGURADO_NAO_INFORMADO.getMessage());
		
		validaLimiteDeIdadePorEspecialidade(segurado, especialidade);
		
		GuiaConsulta guiaConsulta = super.criarGuiaLancamento(segurado, TipoConsultaEnum.NORMAL, dataDeAtendimento, prestador, profissional, especialidade, usuario); 
		
		aplicarAcordoConsultaEspecialidade(prestador, especialidade, guiaConsulta);
		
		return guiaConsulta;
	}
	
	public GuiaConsulta criarGuiaLancamento(Segurado segurado, String dataDeAtendimento, Prestador prestador, 
		Profissional profissionalCRM, Profissional profissionalNOME, Especialidade especialidade,  UsuarioInterface usuario) throws Exception {
		
		Assert.isNotNull(segurado, MensagemErroEnum.SEGURADO_NAO_INFORMADO.getMessage());
		
		if(segurado.getDataAdesao() == null){
			throw new RuntimeException(MensagemErroEnumSR.SEGURADO_SEM_DATA_DE_ADESAO.getMessage());
		}
		
		if(segurado.getSituacao().getDescricao().equals(SituacaoEnum.SUSPENSO)
				&& segurado.getSituacao().getMotivo().equals("Não recadastrado")) {
				throw new ValidateException(MensagemErroEnumSR.SEGURADO_NAO_RECADASTRADO.getMessage());
		}
		
		Profissional profissional = ProfissionalUtils.getProfissionais(profissionalCRM, profissionalNOME);
		if(!prestador.getProfissionais().contains(profissional)) {
			throw new RuntimeException(MensagemErroEnum.PROFISSIONAl_NAO_ASSOCIADO_AO_PRESTADOR.getMessage(profissional.getPessoaFisica().getNome(),prestador.getPessoaJuridica().getFantasia()));
		}
		
		validaLimiteDeIdadePorEspecialidade(segurado, especialidade);
		/**
		 * Faz a validação de intervalo do limite de idade permitido para utilização de atendimento por especialidade.
		 * Quando não houver valor informado, ou seja estiver nulo, esta restrição não será executada.
		 * Ex: até 14 anos para especialidade (Pediatria)
		 *     acima de 60 anos (Geriatria)
		 */
		if(especialidade.getIdadeLimiteInicio()!=null || especialidade.getIdadeLimiteFim()!=null) {
			Integer idadeLimiteInicio = especialidade.getIdadeLimiteInicio();
			Integer idadeLimiteFim = especialidade.getIdadeLimiteFim();
			Integer idadeSegurado = Utils.getIdade(segurado.getPessoaFisica().getDataNascimento());
			
			boolean isIdadeSeguradoMaiorIgualIdadeLimite = idadeSegurado>=idadeLimiteInicio;
			boolean isIdadeSeguradoMenorIgualIdadeLimite = idadeSegurado<=idadeLimiteFim;
			
			if(!(isIdadeSeguradoMaiorIgualIdadeLimite && isIdadeSeguradoMenorIgualIdadeLimite)){
				throw new RuntimeException(MensagemErroEnum.LIMITE_IDADE_NAO_PERMITIDO_ESPECIALIDADE.getMessage(
						idadeSegurado.toString(), idadeLimiteInicio.toString(), idadeLimiteFim.toString()));
			}
		}
		
		GuiaConsulta guiaConsulta = super.criarGuiaLancamento(segurado, TipoConsultaEnum.NORMAL, dataDeAtendimento, prestador, profissional, especialidade, usuario);
		
		aplicarAcordoConsultaEspecialidade(prestador, especialidade, guiaConsulta);
		
		return guiaConsulta;
	}
	
	private void validaLimiteDeIdadePorEspecialidade(Segurado segurado, Especialidade especialidade){
		/**
		 * Faz a validação de intervalo do limite de idade permitido para utilização de atendimento por especialidade.
		 * Quando não houver valor informado, ou seja estiver nulo, esta restrição não será executada.
		 * Ex: até 14 anos para especialidade (Pediatria)
		 *     acima de 60 anos (Geriatria)
		 */
		if(especialidade.getIdadeLimiteInicio()!=null || especialidade.getIdadeLimiteFim()!=null) {
			Integer idadeLimiteInicio = especialidade.getIdadeLimiteInicio();
			Integer idadeLimiteFim = especialidade.getIdadeLimiteFim();
			Integer idadeSegurado = Utils.getIdade(segurado.getPessoaFisica().getDataNascimento());
			
			boolean isIdadeSeguradoMaiorIgualIdadeLimite = idadeSegurado>=idadeLimiteInicio;
			boolean isIdadeSeguradoMenorIgualIdadeLimite = idadeSegurado<=idadeLimiteFim;
			
			if(!(isIdadeSeguradoMaiorIgualIdadeLimite && isIdadeSeguradoMenorIgualIdadeLimite)){
				throw new RuntimeException(MensagemErroEnum.LIMITE_IDADE_NAO_PERMITIDO_ESPECIALIDADE.getMessage(
						idadeSegurado.toString(), idadeLimiteInicio.toString(), idadeLimiteFim.toString()));
			}
		}
	}	
		
	@Override
	public void onAfterCriarGuia(GuiaConsulta<ProcedimentoInterface> guia, Collection<? extends ProcedimentoInterface> procedimentos) throws Exception {
		Service.verificaRecadastramento(guia);
		guia.setDataTerminoAtendimento(guia.getDataAtendimento());
		super.onAfterCriarGuia(guia, procedimentos);
	}
	
}