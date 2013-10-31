package br.com.infoway.ecare.services.tarefasCorrecao.migracaoNovaTabela;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;

import org.hibernate.Session;
import org.hibernate.Transaction;

import br.com.infowaypi.ecarebc.financeiro.arquivos.ArquivoInterface;
import br.com.infowaypi.ecarebc.procedimentos.Porte;
import br.com.infowaypi.ecarebc.procedimentos.PorteAnestesicoEnum;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.ecarebc.procedimentos.TipoAcomodacao;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe que migra os dados da tabela parametrizada enviada pela Dr. Deildes para o banco.
 * A classe não migra especialidades nem procedimentos incompatíveis.
 * 
 * @author Eduardo
 *
 */
@SuppressWarnings("unchecked")
public class MigracaoTPSR {
	
	private static final File FILE = new File(ArquivoInterface.REPOSITORIO_MIGRACAO_TPSR+"planilhaTPSR.xls");
	
	static Map<String, TabelaCBHPM> mapProcedimentosBanco = new HashMap<String, TabelaCBHPM>();
	static Map<String, Porte> mapPorteBanco = new HashMap<String, Porte>();
	static Map<String, TipoAcomodacao> mapAcomodacao = new HashMap<String, TipoAcomodacao>();
	
	static final File ERROR_LOG = new File(ArquivoInterface.REPOSITORIO_MIGRACAO_TPSR+"error_log.csv");
	static final File EDIT_LOG 	= new File(ArquivoInterface.REPOSITORIO_MIGRACAO_TPSR+"edit_log.csv");
	
	static ConversorDeDadosTPSR conversor = new ConversorDeDadosTPSR();
	static Logger logErros;
	static Logger logAlteracoes;
	
	static final String TIPO_UTI 			= "UTI";
	static final String TIPO_DAYCLINIC		= "DAYCLINIC";
	static final String TIPO_BERCARIO 		= "BERÇARIO";
	static final String TIPO_ENFERMARIA 	= "ENFERMARIA";

	public static void main(String[] args) throws Exception {
		
		montarMapaDeProcedimentos();
		montarMapaDePortes();
		montarMapaAcomodacao();
		
		Session session = HibernateUtil.currentSession();
		Transaction transaction = session.beginTransaction();
		
		logErros = new Logger(ERROR_LOG);
		logAlteracoes = new Logger(EDIT_LOG);
		
		Workbook arquivo = Workbook.getWorkbook(FILE);
		Sheet planilha = arquivo.getSheet(0);
		int linha = 2;
		while (true) {
			try {
				String codigoDoProcedimento = conversor.processaString(planilha.getCell(0, linha).getContents());
				try {
					conversor.processaQuantidade(codigoDoProcedimento);
				} catch (Exception e) {
					linha++;
					continue;
				}
				boolean isVazio = Utils.isStringVazia(codigoDoProcedimento);
				if (isVazio) {
					try {
						String codigoDoProximoProcedimento = planilha.getCell(0, linha+1).getContents();
						if (isVazio & Utils.isStringVazia(codigoDoProximoProcedimento)) {
							break;
						}
					} catch (ArrayIndexOutOfBoundsException e) {
						break;
					}
					linha++;
					continue;
				}
				
				if (mapProcedimentosBanco.containsKey(codigoDoProcedimento)) {
					System.out.print("Procedimento: " + codigoDoProcedimento + ": ");
					
					logErros.log(codigoDoProcedimento);
					
					TabelaCBHPM procedimento = mapProcedimentosBanco.get(codigoDoProcedimento);
					if (procedimento != null) {
						updateProcedimento(planilha, linha, codigoDoProcedimento, procedimento);
						setRelacionamentos(planilha, linha, procedimento);
						ImplDAO.save(procedimento);
					} else {
						logErros.log("Procedimento não cadastrado no banco: " + codigoDoProcedimento);
					}
					
					logErros.newRecord();
					logAlteracoes.newRecord();

					System.out.println("OK");
				}
				
				linha++;
			} catch (ArrayIndexOutOfBoundsException e) {
				break;
			}
		}
		arquivo.close();
	
		transaction.commit();
		
		logErros.save();
		logAlteracoes.save();
	}

	private static void montarMapaAcomodacao() {
		System.out.print("Criando mapa de acomodacoes... ");
		List<TipoAcomodacao> acomodacoes = HibernateUtil.currentSession().createCriteria(TipoAcomodacao.class).list();
		for (TipoAcomodacao tipoAcomodacao : acomodacoes) {
			mapAcomodacao.put(tipoAcomodacao.getDescricao(), tipoAcomodacao);
		}
		System.out.println("OK");
	}

	private static void montarMapaDePortes() {
		System.out.print("Criando mapa de portes... ");
		List<Porte> portes = HibernateUtil.currentSession().createCriteria(Porte.class).list();
		for (Porte porte : portes) {
			mapPorteBanco.put(porte.getDescricao(), porte);
		}
		System.out.println("OK");
	}

	private static void montarMapaDeProcedimentos() {
		System.out.print("Criando mapa de procedimentos... ");
		List<TabelaCBHPM> procedimentos = HibernateUtil.currentSession().createCriteria(TabelaCBHPM.class).list();
		for (TabelaCBHPM tabelaCBHPM : procedimentos) {
			mapProcedimentosBanco.put(tabelaCBHPM.getCodigo(), tabelaCBHPM);
		}
		System.out.println("OK");
	}
	
	private static void updateProcedimento(Sheet planilha, int linha, String codigoDoProcedimento, TabelaCBHPM procedimento) {
		
		logAlteracoes.log("Codigo: " + codigoDoProcedimento);
		
		logAlteracoes.log("Descrição: " + procedimento.getDescricao());
		logErros.log(procedimento.getDescricao());
		
		
		/* AINDA NAO ENTROU
		Boolean exigeAutorizacao = conversor.processaSimNao(planilha.getCell(4, linha).getContents());
		if (exigeAutorizacao != null) {
			procedimento.setExigeAutorizacao(exigeAutorizacao);
			logAlteracoes.log("Exige autorização: " + exigeAutorizacao);
		} else {
			procedimento.setExigeAutorizacao(false);
			logErros.log("Exige autorizacao: " + planilha.getCell(4, linha).getContents());
		}*/
		
		//NO AR
		boolean exigePericia = conversor.processaMarcacao(planilha.getCell(5, linha).getContents());
		procedimento.setExigePericia(exigePericia);
		logAlteracoes.log("Exige pericia: " + exigePericia);
		
		/* AINDA NAO ENTROU
		Integer nivelAutorizacao = conversor.processaQuantidade(planilha.getCell(6, linha).getContents());
		if (nivelAutorizacao != null) {
			procedimento.setNivelDeAutorizacao(nivelAutorizacao);
			logAlteracoes.log("Nivel de autorização: " + nivelAutorizacao);
		} else {
			logErros.log("nivel de autorizacao: " + planilha.getCell(6, linha).getContents());
		}*/
			
		//NO AR
		Integer numeroAuxiliares = conversor.processaQuantidade(planilha.getCell(8, linha).getContents());
		if (numeroAuxiliares != null) {
			procedimento.setQuantidadeDeAuxilios(numeroAuxiliares);
			logAlteracoes.log("Numero de auxilios: " + numeroAuxiliares);
		} else {
			logErros.log("Numero de auxilios: " + planilha.getCell(8, linha).getContents());
		}
		
		//NO AR
		Integer tempoPermanencia = conversor.processaQuantidade(planilha.getCell(12, linha).getContents());
		if (tempoPermanencia != null) {
			procedimento.setTempoPermanencia(tempoPermanencia);
			logAlteracoes.log("Tempo permanencia: " + tempoPermanencia);
		} else {
			logErros.log("Tempo de permanencia: " + planilha.getCell(12, linha).getContents());
		}
		
		Integer idadeMinima = conversor.processaQuantidade(planilha.getCell(20, linha).getContents());
		if (idadeMinima != null) {
			procedimento.setIdadeMinima(idadeMinima);
			logAlteracoes.log("Idade minima: " + idadeMinima);
		} else {
			logErros.log("Idade minima: " + planilha.getCell(20, linha).getContents());
		}
		
		Integer idadeMaxima = conversor.processaQuantidade(planilha.getCell(21, linha).getContents());
		if (idadeMaxima != null) {
			procedimento.setIdadeMaxima(idadeMaxima);
			logAlteracoes.log("Idade maxima: " + idadeMaxima);
		} else {
			logErros.log("Idade máxima: " + planilha.getCell(21, linha).getContents());
		}
		
		Integer sexo = conversor.processaSexo(planilha.getCell(22, linha).getContents());
		if (sexo != null) {
			procedimento.setSexo(sexo);
			logAlteracoes.log("Sexo: " + sexo);
		} else {
			logErros.log("Sexo: " + planilha.getCell(22, linha).getContents());
		}
		
		Integer periodicidade = conversor.processaQuantidade(planilha.getCell(26, linha).getContents());
		if (periodicidade != null) {
			procedimento.setPeriodicidade(periodicidade);
			logAlteracoes.log("Periodicidade: " + periodicidade);
		} else {
			logErros.log("Periodicidade: "+ planilha.getCell(26, linha).getContents());
		}
		
		/* AINDA NAO FOI ALTERADO
		Integer unicidade = conversor.processaUnicidade(planilha.getCell(27, linha).getContents());
		if (unicidade != null) {
			procedimento.setUnicidade(unicidade);
			logAlteracoes.log("Unicidade: " + unicidade);
		} else {
			logErros.log("Unicidade: " + planilha.getCell(27, linha).getContents());
		}*/
			
		BigDecimal moderacao = conversor.processaValor(planilha.getCell(28, linha).getContents());
		if (moderacao != null) {
			procedimento.setModeracao(moderacao.floatValue());
			logAlteracoes.log("Moderacao: " + moderacao);
		} else {
			logErros.log("Moderacao: " + planilha.getCell(28, linha).getContents());
		}
		
		BigDecimal valorPago = conversor.processaValor(planilha.getCell(30, linha).getContents());
		if (valorPago != null) {
			procedimento.setValorModerado(valorPago);
			logAlteracoes.log("VAlor pago: " + valorPago);
		} else {
			logErros.log("Valor pago: " + planilha.getCell(30, linha).getContents());
		}
			
		Boolean permiteMedicamentos = conversor.processaSimNao(planilha.getCell(31, linha).getContents());
		if (permiteMedicamentos != null) {
			procedimento.setPermiteMedicamentoComplementar(permiteMedicamentos);
			logAlteracoes.log("Permite medicamentos: " + permiteMedicamentos);
		} else {
			logErros.log("Permite medicamentos: " + planilha.getCell(31, linha).getContents());
		}
		
		Boolean permiteMateriais = conversor.processaSimNao(planilha.getCell(32, linha).getContents());
		if (permiteMateriais != null) {
			procedimento.setPermiteMaterialComplementar(permiteMateriais);
			logAlteracoes.log("Permite materiais: " + permiteMateriais);
		} else {
			logErros.log("Permite materiais: " + planilha.getCell(32, linha).getContents());
		}
		
		Integer carencia = conversor.processaQuantidade(planilha.getCell(33, linha).getContents());
		if (carencia != null) {
			procedimento.setCarencia(carencia);
			logAlteracoes.log("Carencia: "  + carencia);
		} else {
			logErros.log("Carencia: " + planilha.getCell(33, linha).getContents());
		}
		
		Integer elementoAplicado = conversor.processaQuantidade(planilha.getCell(34, linha).getContents());
		if (elementoAplicado != null) {
			procedimento.setElementoAplicado(elementoAplicado);
			logAlteracoes.log("Elemento aplicado: " + elementoAplicado);
		} else {
			logErros.log("Elemento aplicado: " + planilha.getCell(34, linha).getContents());
		}
		
		Boolean verificaPeriodicidadeNaInternacao = conversor.processaSimNao(planilha.getCell(35, linha).getContents());
		if (verificaPeriodicidadeNaInternacao != null) {
			procedimento.setVerificaPeriodicidadeNaInternacao(verificaPeriodicidadeNaInternacao);
			logAlteracoes.log("Periodicidade na internacao: " + verificaPeriodicidadeNaInternacao);
		} else {
			logErros.log("Periodicidade na internacao: " + planilha.getCell(35, linha).getContents());
		}
		
		Boolean bilateral = conversor.processaSimNao(planilha.getCell(36, linha).getContents());
		if (bilateral != null) {
			procedimento.setBilateral(bilateral);
			logAlteracoes.log("Bilateral: " + bilateral);
		} else {
			logErros.log("Bilateral: " + planilha.getCell(36, linha).getContents());
		}
		
		//NO AR
		boolean permitePME = conversor.processaMarcacao(planilha.getCell(13, linha).getContents());
		procedimento.setPermitePme(permitePME);
		logAlteracoes.log("Permite PME: " + permitePME);
		
		/* AINDA NÃO FOI IMPLANTADO
		Integer nivelComplexidade = conversor.processaComplexidade(planilha.getCell(11, linha).getContents());
		if (nivelComplexidade != null) {
			procedimento.setNivelDeComplexidade(nivelComplexidade);
			logAlteracoes.log("Nivel complexidade: " + nivelComplexidade);
		} else {
			logErros.log("Nivel complexidade: " + planilha.getCell(11, linha).getContents());
		}*/
		
	}

	private static void setRelacionamentos(Sheet planilha, int linha,
			TabelaCBHPM procedimento) {
		
		String porte = conversor.processaPorte(planilha.getCell(7, linha).getContents());
		if (!Utils.isStringVazia(porte)) {
			Porte porte2 = mapPorteBanco.get(porte);
			if (porte2 != null) {
				procedimento.setPorte(porte2);
				logAlteracoes.log("Porte: " + porte);
			} else {
				logErros.log("Porte na cadastrado: "+ planilha.getCell(7, linha).getContents());
			}
		} else {
			logErros.log("Porte: "+ planilha.getCell(7, linha).getContents());
		}
		
		Integer porteAnestesico = conversor.processaQuantidade(planilha.getCell(9, linha).getContents());
		if (porteAnestesico != null) {
			Porte porte2 = mapPorteBanco.get(PorteAnestesicoEnum.getDescricao(porteAnestesico));
			if (porte2 != null) {
				procedimento.setPorteAnestesico(porte2);
				logAlteracoes.log("porte anestesico: " + porteAnestesico);
			} else {
				logErros.log("Porte anestesico nao cadastrado: "+ planilha.getCell(9, linha).getContents());
			}
		} else {
			logErros.log("Porte anestesico: "+ planilha.getCell(9, linha).getContents());
		}
		
		/*
		 * A MIGRAÇÃO DESSE CAMPO JÁ RODOU, AINDA QUE NO MOMENTO ERRADO
		Integer localAtendimento = conversor.processaLocal(planilha.getCell(14, linha).getContents());
		if (localAtendimento != null) {
			procedimento.setLocalDeAtendimento(localAtendimento);
			logAlteracoes.log("Local de atendimento: " + localAtendimento);
		} else {
			logErros.log("Local de atendimento: " + planilha.getCell(14, linha).getContents());
		}*/
	
		setAcomodacao(planilha, linha, procedimento);
//		setQuantidadeCBHPM(planilha, linha, procedimento);
	}

	/* AINDA NAO FOI ALTERADO
	private static void setQuantidadeCBHPM(Sheet planilha, int linha, TabelaCBHPM procedimento) {
		Integer quantidadeDia = conversor.processaQuantidade(planilha.getCell(23, linha).getContents());
		if (quantidadeDia != null) {
			procedimento.setQuantidadeDiaria(quantidadeDia);
			logAlteracoes.log("Quantidade diaria: " + quantidadeDia);
		}
	
		Integer quantidadeMes = conversor.processaQuantidade(planilha.getCell(24, linha).getContents());
		if (quantidadeMes != null) {
			procedimento.setQuantidadeMensal(quantidadeMes);
			logAlteracoes.log("Quantidade mensal: " + quantidadeMes);
		}
		
		Integer quantidadeAno = conversor.processaQuantidade(planilha.getCell(25, linha).getContents());
		if (quantidadeAno != null) {
			procedimento.setQuantidadeAnual(quantidadeAno);
			logAlteracoes.log("Quantidade anual: " + quantidadeAno);
		}
	}*/

	private static void setAcomodacao(Sheet planilha, int linha, TabelaCBHPM procedimento) {
		boolean acomodacaoApartamento = conversor.processaMarcacao(planilha.getCell(15, linha).getContents());
		if (acomodacaoApartamento) {
			TipoAcomodacao tipoAcomodacao = mapAcomodacao.get(TIPO_ENFERMARIA);
			if (tipoAcomodacao != null) {
				procedimento.getTiposAcomodacao().add(tipoAcomodacao);
				logAlteracoes.log("Tipo acomodacao: apartamento");
			} else {
				TipoAcomodacao apartamento = TipoAcomodacao.createAcomodacao(TIPO_ENFERMARIA);
				procedimento.getTiposAcomodacao().add(apartamento);
				
				mapAcomodacao.put(apartamento.getDescricao(), apartamento);
				
				logAlteracoes.log("Novo tipo acomodacao: apartamento");
			}
		}
		
		boolean acomodacaoUTI = conversor.processaMarcacao(planilha.getCell(16, linha).getContents());
		if (acomodacaoUTI) {
			TipoAcomodacao tipoAcomodacao = mapAcomodacao.get(TIPO_UTI);
			if (tipoAcomodacao != null) {
				procedimento.getTiposAcomodacao().add(tipoAcomodacao);
				logAlteracoes.log("Tipo acomodacao: UTI");
			} else {
				TipoAcomodacao apartamento = TipoAcomodacao.createAcomodacao(TIPO_UTI);
				procedimento.getTiposAcomodacao().add(apartamento);
				
				mapAcomodacao.put(apartamento.getDescricao(), apartamento);
				
				logAlteracoes.log("Novo tipo acomodacao: UTI");
			}
		}
		
		boolean acomodacaoBercario = conversor.processaMarcacao(planilha.getCell(17, linha).getContents());
		if (acomodacaoBercario) {
			TipoAcomodacao tipoAcomodacao = mapAcomodacao.get(TIPO_BERCARIO);
			if (tipoAcomodacao != null) {
				procedimento.getTiposAcomodacao().add(tipoAcomodacao);
				logAlteracoes.log("Tipo acomodacao: Bercario");
			} else {
				TipoAcomodacao apartamento = TipoAcomodacao.createAcomodacao(TIPO_BERCARIO);
				procedimento.getTiposAcomodacao().add(apartamento);
				
				mapAcomodacao.put(apartamento.getDescricao(), apartamento);
				
				logAlteracoes.log("Novo tipo acomodacao: bercario");
			}
		}
		
		boolean acomodacaoDayClinic = conversor.processaMarcacao(planilha.getCell(18, linha).getContents());
		if (acomodacaoDayClinic) {
			TipoAcomodacao tipoAcomodacao = mapAcomodacao.get(TIPO_DAYCLINIC);
			if (tipoAcomodacao != null) {
				procedimento.getTiposAcomodacao().add(tipoAcomodacao);
				logAlteracoes.log("Tipo acomodacao: day clinic");
			} else {
				TipoAcomodacao apartamento = TipoAcomodacao.createAcomodacao(TIPO_DAYCLINIC);
				procedimento.getTiposAcomodacao().add(apartamento);
				
				mapAcomodacao.put(apartamento.getDescricao(), apartamento);
				
				logAlteracoes.log("Novo tipo acomodacao: enfermaria");
			}
		}
	}
	
}