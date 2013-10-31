package br.com.infowaypi.ecare.arquivos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecare.segurados.DependenteInterface;
import br.com.infowaypi.ecare.segurados.Titular;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.utils.Utils;

/**
 * @author Marcus bOolean
 *
 */
public class ArquivoSegurados {
	
	private List<Titular> titulares = new ArrayList<Titular>();
	
	private StringBuffer fileBody = new StringBuffer();
	
	public ArquivoSegurados(List<Titular> titulares) {
		this.titulares = titulares;
	}
	
//	private Set<Segurado> getTitulares() {
//		Set<Segurado> titularesEncontrados = new HashSet<Segurado>();
//		for (Segurado segurado : segurados) {
//			if(segurado.getTipoDeSegurado().equals(TipoSeguradoEnum.TITULAR.descricao())){
//				titularesEncontrados.add(segurado);
//			}
//		}
//		
//		return titularesEncontrados;
//	}
	
	public void fileGenerate() throws Exception {
		int seguradosProcessados = 0;
		System.out.println("Inicio do processamento de segurados...");
		for (Titular titular : this.titulares) {
			if(titular.getDataAdesao() == null) {
				System.out.println("Titular de CPF: " + titular.getPessoaFisica().getCpf()+ " possui data de adesão nula!");
				seguradosProcessados++;
				continue;
			}		
			
			if(titular.getMatriculas().size() == 0){
				seguradosProcessados++;
				continue;
			}
				
			
			titular.setDataGeracaoDoCartao(new Date());
			ImplDAO.save(titular);
			
			fileBody.append("T");
			fileBody.append(";");
			fileBody.append(titular.getNumeroDoCartao());
			fileBody.append(";");
			fileBody.append(titular.getPessoaFisica().getNome());
			fileBody.append(";");
			fileBody.append(titular.getDataAdesao());
			fileBody.append(";");
			
			fileBody.append(System.getProperty("line.separator"));
			
			seguradosProcessados++;
			
			if(!titular.getDependentes().isEmpty()) {
				for (DependenteInterface dependente : titular.getDependentes()) {
					
					if(!dependente.isRecadastrado()){
						seguradosProcessados++;
						continue;
					}
					dependente.setDataGeracaoDoCartao(new Date());
					ImplDAO.save(dependente);
					fileBody.append("D");
					fileBody.append(";");
					fileBody.append(dependente.getNumeroDoCartao());
					fileBody.append(";");
					fileBody.append(dependente.getPessoaFisica().getNome());
					fileBody.append(";");
					if(dependente.getDataAdesao() != null) {
						fileBody.append(dependente.getDataAdesao());
						fileBody.append(";");
						System.out.println("Dependente de CPF: " + dependente.getPessoaFisica().getCpf()+ " possui data de adesão nula!");
					}
					else{
						fileBody.append(dependente.getTitular().getDataAdesao());
						fileBody.append(";");
					}
					
					fileBody.append(System.getProperty("line.separator"));
					seguradosProcessados++;

				}
			}
		}
		Utils.criarArquivo("C:/ArquivoDeSegurados", "", this.fileBody);
		System.out.println("Processo finalizado com êxito!");
		System.out.println(seguradosProcessados + " segurados processados.");
	}
}
