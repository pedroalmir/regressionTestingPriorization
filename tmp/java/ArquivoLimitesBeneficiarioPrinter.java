package br.com.infowaypi.ecare.arquivos;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import br.com.infowaypi.ecare.services.ResumoLimitesBeneficiario;
import br.com.infowaypi.ecare.services.enuns.TipoLimiteEnum;
import br.com.infowaypi.jheat.reports.JHeatReport;

/** A classe ArquivoLimitesBeneficiarioPrinter produz o array de bytes para a gera��o do Relat�rio de Limites do Benefici�rio.
 * Obs.: Se o template necessitar de altera��o por qualquer raz�o executar os seguintes passos:
 * 1-Trocar getClass().getResource() pelo caminho real de seu sistema para que possa modifica-lo em tempo de execu��o, caso contr�rio
 * qualquer modifica��o no template ter� que, necess�riamente , bildar a aplica��o para ver a altera��o;
 * 2-Comece pelo template LimiteBeneficiario.xml pois dele se originaram os outros;
 * 3 -Quanto terminar as altera��es no template recoloque o getClass().getResource().
 * @author Jefferson
 */
public class ArquivoLimitesBeneficiarioPrinter {
	
	public byte[] createPDF(ResumoLimitesBeneficiario resumo, TipoLimiteEnum tipoLimite) throws Exception {
		List<Object> itens = new ArrayList<Object>();
		itens.add(new DataSourceLimiteBeneficiario(resumo));
		String template = criarTemplate(tipoLimite);
		JHeatReport report = new JHeatReport(template, itens);
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		report.createPDF(byteOutput);
		return byteOutput.toByteArray();
	}
	
	private String criarTemplate(TipoLimiteEnum tipoLimite) {
		String pastaTemplates = "/templates/";
		
		if (tipoLimite.getTipoGuia().equals(TipoLimiteEnum.CONSULTA.getTipoGuia())){
			return  getClass().getResource(pastaTemplates+"LimiteBeneficiarioConsulta.xml").getPath();
		}
		
		if (tipoLimite.getTipoGuia().equals(TipoLimiteEnum.EXAME.getTipoGuia())){
			return getClass().getResource(pastaTemplates+"LimiteBeneficiarioExame.xml").getPath();
		}
		
		if (tipoLimite.getTipoGuia().equals(TipoLimiteEnum.CONSULTA_ODONTOLOGICA.getTipoGuia())){
			return  getClass().getResource(pastaTemplates+"LimiteBeneficiarioConsultaOdontologica.xml").getPath();
		}
		
		if (tipoLimite.getTipoGuia().equals(TipoLimiteEnum.TRATAMENTO_ODONTOLOGICO.getTipoGuia())){
			return getClass().getResource(pastaTemplates+"LimiteBeneficiarioTratamentoOdontologico.xml").getPath();
		}
		
		return  getClass().getResource(pastaTemplates+"LimiteBeneficiario.xml").getPath();
	}
}
