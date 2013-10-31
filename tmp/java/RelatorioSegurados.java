package br.com.infowaypi.ecare.correcaomanual;

import java.util.List;

import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.segurados.TitularFinanceiroSR;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.msr.enums.EstadoCivilEnum;
import br.com.infowaypi.msr.utils.Utils;

public class RelatorioSegurados {

	private static StringBuilder relatorio = null;
	private static final String pontoVirgula = ";";
	private static final String virgula = ",";
	private static final String quebraLinha = "\n";
	private static final String vazio = "";
	
	static{
		relatorio = new StringBuilder(
				"Tipo;ID Titular;Número do Cartão;Matrícula;Sexo;Nome;Nome da Mãe;Data de Nascimento;Identidade;CPF;Telefone Residencial;" +
				"Telefone do Trabalho;Celular;E-mail;Estado Civil;ID Grupo;Via do Cartão;Recadastrado?;Situação;Data de Adesão;Cidade;" +
				"Bairro;Logradouro;Numero;Complemento;CEP;UF" + quebraLinha);
	}
	
	public static void main(String[] args) {
		gerar();
		System.out.println(relatorio.toString());
	}

	private static void gerar() {
		List<Segurado> segurados = getSeguradosAtivos();
		System.out.println("Baixou os dados...");
		
		for (Segurado s : segurados) {
			
			String tipo = s.getTipoDeSegurado();
			String idSegurado = String.valueOf(s.getTitular().getIdSegurado());
			String numeroDoCartao = s.getNumeroDoCartao();
			
			TitularFinanceiroSR titularFinanceiroSR = (TitularFinanceiroSR)s.getTitular();
			String matricula = !titularFinanceiroSR.getMatriculasAtivas().isEmpty()? titularFinanceiroSR.getMatriculasAtivas().iterator().next().getDescricao():"";
			
			String sexoFormatado = s.getSexoFormatado();
			String nome = s.getPessoaFisica().getNome();
			String nomeDaMae = s.getPessoaFisica().getNomeDaMae();
			String dataNescimento = Utils.format(s.getPessoaFisica().getDataNascimento());
			String identidade = s.getPessoaFisica().getIdentidade();
			String cpf = s.getPessoaFisica().getCpf();
			String telefoneResidencial = s.getPessoaFisica().getTelefoneResidencial();
			String telefoneTrabalho = s.getPessoaFisica().getTelefoneDoTrabalho();
			String celular = s.getPessoaFisica().getCelular();
			String email = s.getPessoaFisica().getEmail();
			String estadoCivil = EstadoCivilEnum.descricao(s.getPessoaFisica().getEstadoCivil());
			String grupo = s.getGrupo() != null? s.getGrupo().getCodigoLegado():"";
			String viaDoCartao = String.valueOf(((Integer) (!s.getCartoes().isEmpty()? s.getCartaoAtual().getViaDoCartao() : -1)));
			String recadastrado = s.isRecadastrado() == true ? "SIM" : "NÃO";
			String descricao = s.getSituacao().getDescricao();
			String dataAdesao = s.getDataAdesao() != null? Utils.format(s.getDataAdesao()) : "";
			String municipio = s.getPessoaFisica().getEndereco().getMunicipio()!= null? s.getPessoaFisica().getEndereco().getMunicipio().getDescricao():"";
			String bairro = s.getPessoaFisica().getEndereco().getBairro();
			String logradouroFormatado = s.getPessoaFisica().getEndereco().getLogradouro();
			String numero = s.getPessoaFisica().getEndereco()!= null? s.getPessoaFisica().getEndereco().getNumero():"";
			String complemento = s.getPessoaFisica().getEndereco().getComplemento();
			String cep = s.getPessoaFisica().getEndereco().getCep();
			
			
			String estado = null;
			
			if(s.getPessoaFisica().getEndereco() != null && s.getPessoaFisica().getEndereco().getMunicipio() != null
					&& s.getPessoaFisica().getEndereco().getMunicipio().getEstado() != null){
				estado = s.getPessoaFisica().getEndereco().getMunicipio().getEstado().getDescricao();
			}
			
			String linha = getCampoSemVirgula(tipo) + pontoVirgula 
					+ getCampoSemVirgula(idSegurado) + pontoVirgula
					+ getCampoSemVirgula(numeroDoCartao) + pontoVirgula
					+ getCampoSemVirgula(matricula) + pontoVirgula
					+ getCampoSemVirgula(sexoFormatado) + pontoVirgula
					+ getCampoSemVirgula(nome) + pontoVirgula
					+ getCampoSemVirgula(nomeDaMae) + pontoVirgula
					+ getCampoSemVirgula(dataNescimento) + pontoVirgula
					+ getCampoSemVirgula(identidade) + pontoVirgula
					+ getCampoSemVirgula(cpf) + pontoVirgula
					+ getCampoSemVirgula(telefoneResidencial) + pontoVirgula
					+ getCampoSemVirgula(telefoneTrabalho) + pontoVirgula
					+ getCampoSemVirgula(celular) + pontoVirgula
					+ getCampoSemVirgula(email) + pontoVirgula
					+ getCampoSemVirgula(estadoCivil) + pontoVirgula
					+ getCampoSemVirgula(grupo) + pontoVirgula
					+ getCampoSemVirgula(viaDoCartao) + pontoVirgula
					+ getCampoSemVirgula(recadastrado) + pontoVirgula
					+ getCampoSemVirgula(descricao) + pontoVirgula
					+ getCampoSemVirgula(dataAdesao) + pontoVirgula
					+ getCampoSemVirgula(municipio) + pontoVirgula
					+ getCampoSemVirgula(bairro) + pontoVirgula
					+ getCampoSemVirgula(logradouroFormatado) + pontoVirgula
					+ getCampoSemVirgula(numero) + pontoVirgula
					+ getCampoSemVirgula(complemento) + pontoVirgula
					+ getCampoSemVirgula(cep) + pontoVirgula
					+ getCampoSemVirgula(estado) + quebraLinha;
					
			relatorio.append(linha);
		}
	}
	
	private static String getCampoSemVirgula(String campo){
		if(campo != null){
			return campo.replaceAll(pontoVirgula, virgula);
		}
		return vazio;
	}

	private static List<Segurado> getSeguradosAtivos() {
		SearchAgent sa = new SearchAgent();
//		sa.addParameter(new Equals("situacao.descricao", SituacaoEnum.ATIVO.descricao()));
		return sa.list(Segurado.class);
	}
	
}
