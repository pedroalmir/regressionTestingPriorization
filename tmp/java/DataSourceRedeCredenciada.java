package br.com.infowaypi.ecare.arquivos;

import java.util.ArrayList;
import java.util.List;

import br.com.infowaypi.ecarebc.associados.Prestador;

/**
 * Classe que representa os dados que serao visualizados no arquivo gerado pelo JHeatReports
 * @author Emanuel
 *
 */
public class DataSourceRedeCredenciada{
	
	List<Prestador> prestadores;
	
	public DataSourceRedeCredenciada(List<Prestador> prestadores){
		this.prestadores = prestadores;
	}
	
	public List<List<String>> getPrestadoresEncontrados() {
		List<List<String>> tabela = new ArrayList<List<String>>();
		List<String> linha;
		
		for (Prestador p : prestadores) {
			linha = new ArrayList<String>();
			linha.add(p.getPessoaJuridica().getFantasia());
			String logradouro = p.getPessoaJuridica().getEndereco().getLogradouro();
			linha.add(logradouro != null ? logradouro : "");
			String bairro = p.getPessoaJuridica().getEndereco().getBairro();
			linha.add(bairro != null ? bairro : "");
			String numero = p.getPessoaJuridica().getEndereco().getNumero();
			linha.add(numero != null ? numero : "");
			String telefone = p.getPessoaJuridica().getTelefone();
			linha.add(telefone != null ? telefone : "");
			tabela.add(linha);
		}
			
		return tabela;
	}
	
	public String getLogo(){
		return getClass().getResource("/templates/saude-recife-logo.png").getPath();
	}
}
