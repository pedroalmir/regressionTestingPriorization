package br.com.infowaypi.ecare.financeiro.boletos;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecare.constantes.Constantes;
import br.com.infowaypi.ecare.segurados.DependenteSuplementar;
import br.com.infowaypi.ecare.segurados.TitularFinanceiroSR;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.address.MunicipioInterface;

public class GeracaoXLSSacados {
	
	private ByteArrayOutputStream sacadosXls;
	
	public byte[] getSacadosXls() {
		return sacadosXls.toByteArray();
	}

	private Set<TitularFinanceiroSR> buscaSacados(){
		Set<TitularFinanceiroSR> sacados = new HashSet<TitularFinanceiroSR>();
		Transaction t = HibernateUtil.currentSession().beginTransaction();
		Criteria criteria = HibernateUtil.currentSession().createCriteria(TitularFinanceiroSR.class);
		criteria.createAlias("matriculas", "mats");
		criteria.add(Expression.eq("mats.tipoPagamento", Constantes.BOLETO));
		criteria.add(Expression.eq("mats.ativa", true));
		criteria.add(Expression.in("situacao.descricao",Arrays.asList(SituacaoEnum.ATIVO.descricao(),SituacaoEnum.SUSPENSO.descricao())));
		sacados.addAll(criteria.list());
		
		Criteria criteria2 = HibernateUtil.currentSession().createCriteria(DependenteSuplementar.class);
		criteria2.add(Expression.in("situacao.descricao",Arrays.asList(SituacaoEnum.ATIVO.descricao(),SituacaoEnum.SUSPENSO.descricao())));
		List<DependenteSuplementar> l2 = criteria2.list();
		for (DependenteSuplementar dep : l2) {
			sacados.add(dep.getTitular());
		}
		
		return sacados;
	}
	
	public GeracaoXLSSacados geracaoXlsSacados() throws IOException, RowsExceededException, WriteException{
		Set<TitularFinanceiroSR> sacados = this.buscaSacados();
		MunicipioInterface municipio = null; 
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		WritableWorkbook workbook = Workbook.createWorkbook(outputStream);
		WritableSheet s = workbook.createSheet("Sacados", 0);
		
		/* Formata a fonte */
		WritableFont wf = new WritableFont(WritableFont.TIMES, 12, WritableFont.BOLD);
		WritableCellFormat cf = new WritableCellFormat(wf);
		cf.setWrap(true);
		
		int linha = 0;
		int coluna = 0;
		s.addCell(new Label(coluna++, linha, "NÚMERO DO CARTÃO",cf));
		s.addCell(new Label(coluna++, linha, "NOME",cf));
		s.addCell(new Label(coluna++, linha, "TIPO DE SEGURADO",cf));
		s.addCell(new Label(coluna++, linha, "BAIRRO",cf));
		s.addCell(new Label(coluna++, linha, "NÚMERO",cf));
		s.addCell(new Label(coluna++, linha, "COMPLEMENTO",cf));
		s.addCell(new Label(coluna++, linha, "CEP",cf));
		s.addCell(new Label(coluna++, linha, "PONTO DE REFERÊNCIA",cf));
		s.addCell(new Label(coluna++, linha, "CIDADE",cf));
		s.addCell(new Label(coluna++, linha, "UF",cf));
		
		for (TitularFinanceiroSR sacado : sacados) {
			linha++;
			coluna = 0;
			s.addCell(new Label(coluna++, linha, sacado.getNumeroDoCartao()));
			s.addCell(new Label(coluna++, linha, sacado.getNomeFormatado()));
			s.addCell(new Label(coluna++, linha, sacado.getTipoDeSegurado()));
			s.addCell(new Label(coluna++, linha, sacado.getPessoaFisica().getEndereco().getBairro()));
			s.addCell(new Label(coluna++, linha, sacado.getPessoaFisica().getEndereco().getNumero()));
			s.addCell(new Label(coluna++, linha, sacado.getPessoaFisica().getEndereco().getComplemento()));
			s.addCell(new Label(coluna++, linha, sacado.getPessoaFisica().getEndereco().getCep()));
			s.addCell(new Label(coluna++, linha, sacado.getPessoaFisica().getEndereco().getPontoDeReferencia()));
			municipio = sacado.getPessoaFisica().getEndereco().getMunicipio();
			s.addCell(new Label(coluna++, linha, municipio == null? "" : municipio.getDescricao()));
			s.addCell(new Label(coluna++, linha, municipio == null? "" : municipio.getEstado().getDescricao()));
		}
		workbook.write();
		workbook.close();
		
		sacadosXls = outputStream;
		
		return this;
	}
	
	public String getFileSacados(){
		return "sacados.xls";
	}

}
