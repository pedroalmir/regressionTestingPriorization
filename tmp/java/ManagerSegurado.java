package br.com.infowaypi.ecare.segurados;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import br.com.infowaypi.ecare.constantes.Constantes;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.Usuario;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;

public class ManagerSegurado {

	public static final int DIFERENCA_MAXIMA_EM_DIAS_REATIVACAO = -3;
	public static final int PERIODO_MAXIMA_EM_DIAS_REATIVACAO = -30;

	public static void suspenderTitular(Segurado segurado, String motivo, UsuarioInterface usuario) throws Exception {
		boolean isSeguradoAtivo = segurado.isSituacaoAtual(SituacaoEnum.ATIVO.descricao());

		if(!isSeguradoAtivo){
			throw new ValidateException("Só é possível suspender segurados em situação Ativo(a)");
		}

		if (Utils.isStringVazia(motivo)){
			throw new Exception("O motivo deve ser preenchido!");
		}
		
		if (segurado.isSeguradoTitular()){
			suspenderDependentesDoSegurado(usuario,Constantes.SITUACAO_SUSPENSO,motivo, new Date(),(Titular) segurado);
		}
		segurado.mudarSituacao(usuario, Constantes.SITUACAO_SUSPENSO, motivo, new Date());
	}

	private static void suspenderDependentesDoSegurado(UsuarioInterface usuario, String situacaoSuspenso, String motivo, Date date, Titular titular) {
		Iterator<DependenteSR> iterator = titular.getDependentes().iterator();
		while (iterator.hasNext()) {
			DependenteSR dependenteSR = (DependenteSR) iterator.next();
			boolean situacaoAtivo = dependenteSR.getSituacao().getDescricao().equals(SituacaoEnum.ATIVO.descricao());
			if (situacaoAtivo){
			      dependenteSR.mudarSituacao(usuario, Constantes.SITUACAO_SUSPENSO, motivo, new Date());
			}
		}

	}

	public static void reativarTitular(Segurado segurado, String motivo, Date dataAdesao,UsuarioInterface usuario) throws Exception {

		boolean isSeguradoCancelado = segurado.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao());
		boolean isSeguradoSuspenso 	= segurado.isSituacaoAtual(SituacaoEnum.SUSPENSO.descricao());

		if(!isSeguradoCancelado && !isSeguradoSuspenso){
			throw new ValidateException("Só é possível reativar segurados em situação Cancelado(a) ou Suspenso(a)");
		}

		if (Utils.isStringVazia(motivo)){
			throw new Exception("O motivo deve ser preenchido!");
		}

		if(segurado.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao())){
			Calendar calendarDataAtual 	= Calendar.getInstance();
			if(dataAdesao != null){
				Calendar calendarDataAdesao = Calendar.getInstance();
				calendarDataAdesao.setTime(dataAdesao);

				int diferencaEmDias = Utils.diferencaEmDias(calendarDataAtual, calendarDataAdesao);

				if(diferencaEmDias < DIFERENCA_MAXIMA_EM_DIAS_REATIVACAO){
					throw new ValidateException("A nova data de adesão não pode ser inferior a três dias da data atual");
				}

				Date dataAdesaoAnterior = segurado.getDataAdesaoAnterior();
				StringBuilder stringBuilder = new StringBuilder().append(" Data de adesão anterior: ")
				.append(Utils.format(dataAdesaoAnterior))
				.append("<br/> Motivo da alteração: ")
				.append(motivo);

				String mensagem = stringBuilder.toString();

				segurado.getColecaoAlteracoes().adicionarAlteracao(mensagem, usuario, new Date());
			}else {
				Calendar calendarDataCancelamento = Calendar.getInstance();
				calendarDataCancelamento.setTime(segurado.getSituacao().getData());

				int diferencaEmDias = Utils.diferencaEmDias(calendarDataAtual, calendarDataCancelamento);

				if(diferencaEmDias < PERIODO_MAXIMA_EM_DIAS_REATIVACAO){
					throw new ValidateException("O beneficiário está cancelado a mais de 30 dias, é necessário informar a nova data de adesão");
				}
			}

			if(dataAdesao != null){
				segurado.setDataAdesao(dataAdesao);
				segurado.setInicioDaCarencia(null);
			}
		}
		if (segurado.isSeguradoTitular()){
			reativarDependentesDoSegurado(usuario,Constantes.SITUACAO_SUSPENSO,motivo, new Date(),(Titular) segurado);
		}

		segurado.mudarSituacao(usuario, Constantes.SITUACAO_ATIVO, motivo, new Date());
		
		
		/**
		 * Usado para reativar o usuário do segurado no portal bo beneficiário.
		 */
		UsuarioInterface usuarioSegurado = segurado.getUsuario();
		usuarioSegurado.setStatus(Usuario.ATIVO);
	}

	private static void reativarDependentesDoSegurado(UsuarioInterface usuario,
			String situacaoSuspenso, String motivo, Date date, Titular segurado) {
		Iterator<DependenteSR> iterator = segurado.getDependentes().iterator();
		String motivoSuspensaoCancelamentoTitular = segurado.getSituacao().getMotivo();
		Date dataSuspensaoOucancelamentoTitular = segurado.getSituacao().getData();
		while (iterator.hasNext()) {
			DependenteSR dependenteSR = (DependenteSR) iterator.next();
			boolean situacaoSuspensoOuCancelado = dependenteSR.getSituacao().getDescricao().equals(SituacaoEnum.SUSPENSO.descricao()) || dependenteSR.getSituacao().getDescricao().equals(SituacaoEnum.CANCELADO.descricao());
			String motivoSuspensaoCancelamentoDependente =  dependenteSR.getSituacao().getMotivo();
			Date dataSuspensaoOucancelamentoDependente = dependenteSR.getSituacao().getData();
			boolean mesmoMotivo = motivoSuspensaoCancelamentoTitular.equals(motivoSuspensaoCancelamentoDependente);
			boolean mesmaData = Utils.compareData(dataSuspensaoOucancelamentoTitular, dataSuspensaoOucancelamentoDependente) == 0;
			if (situacaoSuspensoOuCancelado && mesmoMotivo && mesmaData ) {
				dependenteSR.mudarSituacao(usuario, Constantes.SITUACAO_ATIVO, motivo, new Date());
			}
			UsuarioInterface usuarioSegurado = segurado.getUsuario();
			usuarioSegurado.setStatus(Usuario.ATIVO);
		}
		
	}

}
