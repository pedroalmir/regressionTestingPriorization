package br.com.infowaypi.ecarebc.procedimentos.validators;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.Role;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;

/**
 * Classe para valida��o de permiss�es de marca��o de procedimentos especiais para usu�rios do e-Care.
 * <b>Procedimento Especial</b> no e-Care � um procedimento da tabela CBHPM que, por ter um valor bastante elevado,
 * s� pode ser liberado para marca��o por uma pessoa autorizada pelo plano de sa�de.
 * @author Danilo Nogueira Portela
 */
@SuppressWarnings("unchecked")
public class ProcedimentoEspecialUsuarioValidator<P extends ProcedimentoInterface, G extends GuiaSimples> extends AbstractProcedimentoValidator<P, G>{
 
	public boolean templateValidator(P proc, G guia) throws ValidateException {
	
		if(guia.getPrestador() != null){
			boolean isPrestadorHemopi = guia.getPrestador().getIdPrestador().equals(Prestador.HEMOPI);
			if(isPrestadorHemopi && guia.getPrestador().isPossuiAcordo(proc.getProcedimentoDaTabelaCBHPM())){
				return true;
			}
		}
		
		
		if(!guia.isUrgencia() && (proc.getSituacao().getUsuario()!= null)){
			UsuarioInterface usuarioDaGuia = guia.getUsuarioDoFluxo() != null? guia.getUsuarioDoFluxo(): proc.getSituacao().getUsuario();
			
			Boolean isSuperUsuario = Role.ROOT.getValor().equals(usuarioDaGuia.getRole()) || Role.DIGITADOR.getValor().equals(usuarioDaGuia.getRole()) ||
			Role.AUDITOR.getValor().equals(usuarioDaGuia.getRole()) || Role.CENTRAL_DE_SERVICOS.getValor().equals(usuarioDaGuia.getRole()) || Role.AUDITOR_ODONTO.getValor().equals(usuarioDaGuia.getRole());
			
			Boolean isGuiaSolicitada = false ;
			if(guia.getGuiaOrigem() != null)
				isGuiaSolicitada = guia.getGuiaOrigem().isInternacao() || guia.getGuiaOrigem().isAtendimentoUrgencia() || 
				guia.getGuiaOrigem().isConsultaUrgencia() || guia.isExameOdonto();
			
			TabelaCBHPM cbhpm = proc.getProcedimentoDaTabelaCBHPM();
			if(!isGuiaSolicitada &&(!isSuperUsuario && cbhpm.getEspecial()))
				throw new ValidateException(MensagemErroEnum.PROCEDIMENTO_ESPECIAL_NAO_SOLICITADO.getMessage(cbhpm.getCodigo()));
		}
		return true;
	}
}
