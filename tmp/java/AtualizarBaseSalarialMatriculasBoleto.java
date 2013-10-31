package br.com.infowaypi.ecare.correcaomanual;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.hibernate.Transaction;

import br.com.infowaypi.ecare.segurados.Matricula;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.msr.utils.MoneyCalculation;

public class AtualizarBaseSalarialMatriculasBoleto {

	
	public static void main(String[] args) throws Exception {
		
		Transaction tx = HibernateUtil.currentSession().beginTransaction();
		
		List<String> cpfsArquivo = getCPFs();
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("ativa", true));
		sa.addParameter(new Equals("tipoPagamento", 3));
		sa.addParameter(new In("segurado.pessoaFisica.cpf", cpfsArquivo));
		List<Matricula> matriculas = sa.list(Matricula.class);
		
		System.out.println("Qtd arquivo: " +  cpfsArquivo.size());
		System.out.println("Qtd encontradas: " + matriculas.size());
		
		int booleto = 0;
		int naoBoleto = 0;
		
		int qtdSalariosAtualizados = 0;
		
		for(Matricula matricula : matriculas){
			if(isEntreAsMatriculasASeremAtualizadas("" + matricula.getIdMatricula())){
				BigDecimal novoSalario = getNovoValorSalario(""+matricula.getIdMatricula());
				BigDecimal salarioAntigo = matricula.getSalario();
				
				if(MoneyCalculation.compare(salarioAntigo, novoSalario) != 0){
					qtdSalariosAtualizados++;
					System.out.println("CPF: " + matricula.getSegurado().getPessoaFisica().getCpf() + " - Salario antigo: " + salarioAntigo + "  -  Novo salario:  " + novoSalario);
					matricula.setSalario(novoSalario);
					ImplDAO.save(matricula);
				}
				
				booleto++;
			}else {
//				System.out.println(matricula.getSegurado().getPessoaFisica().getCpf() + " => " + matricula.getIdMatricula());
				naoBoleto++;
			}
		}
		
		System.out.println("\n\nQtd matriculas boleto: " + booleto);
		System.out.println("Qtd matriculas não boleto: " + naoBoleto);
		System.out.println("Qtd matriculas alteradas: " + qtdSalariosAtualizados);
		
		tx.commit();
	}
	
	
	private static boolean isEntreAsMatriculasASeremAtualizadas(String idMatricula) throws FileNotFoundException{
		List<String[]> dadosSegurados = getDadosSegurados();
		for(String[] dadoSegurado : dadosSegurados){
			if(dadoSegurado[1].equals(idMatricula)){
				return true;
			}
		}
		return false;
	}
	
	private static BigDecimal getNovoValorSalario(String idMatricula) throws FileNotFoundException{
		List<String[]> dadosSegurados = getDadosSegurados();
		for(String[] dadoSegurado : dadosSegurados){
			if(dadoSegurado[1].equals(idMatricula)){
				return new BigDecimal(dadoSegurado[2]).setScale(2, BigDecimal.ROUND_HALF_UP);
			}
		}
		
		return null;
	}
	
	private static List<String[]> getDadosSegurados() throws FileNotFoundException{
		List<String[]> segurados = new ArrayList<String[]>(); 
		
		Scanner scanner = new Scanner(new File("C://Users//Infoway//Desktop//atualizacao_base_salarial_bolerto//cpf_idMatricula_salario.csv"));
		while(scanner.hasNext()){
			//CPF, idMatricula, Salário
			String[] segurado = scanner.next().split(";");
			segurado[0].trim();
			segurado[1].trim();
			segurado[2].trim();
			segurados.add(segurado);
		}
		
		return segurados;
	}
	private static List<String> getCPFs() throws FileNotFoundException{
		List<String> matriculas = new ArrayList<String>(); 
		
		Scanner scanner = new Scanner(new File("C://Users//Infoway//Desktop//atualizacao_base_salarial_bolerto//cpfs.txt"));
		while(scanner.hasNext()){
			matriculas.add(scanner.next().trim());
		}
		
		return matriculas;
	}
}
