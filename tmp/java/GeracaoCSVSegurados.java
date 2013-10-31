package br.com.infowaypi.ecare.geracaoCSV;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.com.infowaypi.ecare.segurados.DependenteInterface;
import br.com.infowaypi.ecare.segurados.Matricula;
import br.com.infowaypi.ecarebc.segurados.GrupoBC;
import br.com.infowaypi.msr.address.MunicipioInterface;
import br.com.infowaypi.msr.pessoa.PessoaFisicaInterface;
import br.com.infowaypi.msr.utils.Utils;

public class GeracaoCSVSegurados {
	final SimpleDateFormat dataFormat;
	
	GeracaoCSVSegurados(){
		dataFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
	}
	
	public String makeCSVLine(List<String> fields, char separator) {
		String csvLine = "";
		
		for (String field : fields) {
			csvLine += field + separator;
		}
		
		return csvLine;
	}  
	
	public String makeCSVLine(List<String> fields, char separator, boolean endWithSeparator) {
		String csvLine = makeCSVLine(fields, separator);
		return (endWithSeparator ? csvLine : csvLine.substring(0, csvLine.length() - 1));
	}
	
	public String makeCSVLine(List<String> fields, char separator, boolean endWithSeparator, boolean endWithNewline) {
		String csvLine = makeCSVLine(fields, separator, endWithSeparator);
		return (endWithNewline ? csvLine + "\n" : csvLine);
	}
	
	private String getSexoString(Integer sexo) {
		if (sexo == null) { return ""; }
		else if (sexo.equals(PessoaFisicaInterface.SEXO_MASCULINO)) { return "M"; }
		else if (sexo.equals(PessoaFisicaInterface.SEXO_FEMININO)) { return "F"; }
		else if (sexo.equals(PessoaFisicaInterface.SEXO_AMBOS)) { return "A"; }
		else if (sexo.equals(PessoaFisicaInterface.SEXO_INDEFINIDO)) { return "I"; }
		else { return ""; }
	}
	
	private String getEstadoCivil(Integer estadoCivil) {
		if (estadoCivil == null) { return ""; }
		else if (estadoCivil.equals(PessoaFisicaInterface.ESTADO_CIVIL_OUTROS)) { return "Outro"; }
		else if (estadoCivil.equals(PessoaFisicaInterface.ESTADO_CIVIL_SOLTEIRO)) { return "Solteiro(a)"; }
		else if (estadoCivil.equals(PessoaFisicaInterface.ESTADO_CIVIL_CASADO)) { return "Casado(a)"; }
		else if (estadoCivil.equals(PessoaFisicaInterface.ESTADO_CIVIL_VIUVO)) { return "Viúvo(a)"; }
		else if (estadoCivil.equals(PessoaFisicaInterface.ESTADO_CIVIL_SEPARADO_JUDICIALMENTE)) { return "Separado(a) Judicialmente"; }
		else if (estadoCivil.equals(PessoaFisicaInterface.ESTADO_CIVIL_DIVORCIADO)) { return "Divociado(a)"; }
		else if (estadoCivil.equals(PessoaFisicaInterface.ESTADO_CIVIL_ESTADO_MARITAL)) { return "Estado Marital"; }
		else { return ""; }
	}
	
	private String getDataNascimento(Date data) {
		if (data == null) { 
			return ""; 
		} else { 
			return dataFormat.format(data); 
		}
	}

	private String getIdGrupo(Long idGrupo) {
		if (idGrupo == null) {
			return ""; 
		} else { 
			return String.valueOf(idGrupo); 
		}
	}
	
	public byte[] gerarCSVTitulares(List<Matricula> matriculas) throws Exception {
		StringBuffer bufTitulares = new StringBuffer();
		List<List<String>> csvLinesTitulares = new ArrayList<List<String>>(matriculas.size());
		List<String> csvLine;
		String viaDoCartao = "1";
		
		String idSegurado, numeroDoCartao, matricula_, sexo, nome, nomeDaMae, dataNascimento, dataAdesao;
		String identidade, cpf, telefoneResidencia, telefoneTrabalho, celular, email;
		String estadoCivil, idGrupo, recadastrado, situacao, bairro, cidade;
		String logradouro, numero, complemento, cep, uf;
		MunicipioInterface municipio;

		csvLine = new ArrayList<String>(20);
		csvLine.add("ID Titular");
		csvLine.add("Número do Cartão");
		csvLine.add("Matrícula");
		csvLine.add("Sexo");
		csvLine.add("Nome");
		csvLine.add("Nome da Mãe");
		csvLine.add("Data de Nascimento");
		csvLine.add("Identidade");
		csvLine.add("CPF");
		csvLine.add("Telefone Residencial");
		csvLine.add("Telefone do Trabalho");
		csvLine.add("Celular");
		csvLine.add("E-mail");
		csvLine.add("Estado Civil");
		csvLine.add("ID Grupo");
		csvLine.add("Via do Cartão");
		csvLine.add("Recadastrado?");
		csvLine.add("Situação");
		csvLine.add("Data de Adesão");
		csvLine.add("Cidade");
		csvLine.add("Bairro");
		csvLine.add("Logradouro");
		csvLine.add("Numero");
		csvLine.add("Complemento");
		csvLine.add("CEP");
		csvLine.add("UF");
		
		csvLinesTitulares.add(csvLine);
		
		for (Matricula matricula : matriculas) {
			csvLine = new ArrayList<String>(20);

			idSegurado  			= String.valueOf(matricula.getSegurado().getIdSegurado());
			numeroDoCartao  		= matricula.getSegurado().getNumeroDoCartao();
			matricula_  			= String.valueOf(matricula.getDescricao());
			sexo  					= getSexoString(matricula.getSegurado().getPessoaFisica().getSexo());
			nome  					= matricula.getSegurado().getPessoaFisica().getNome();
			nomeDaMae  				= matricula.getSegurado().getPessoaFisica().getNomeDaMae();
			dataNascimento  		= getDataNascimento(matricula.getSegurado().getPessoaFisica().getDataNascimento());
			identidade  			= matricula.getSegurado().getPessoaFisica().getIdentidade();
			cpf  					= matricula.getSegurado().getPessoaFisica().getCpf();
			telefoneResidencia  	= matricula.getSegurado().getPessoaFisica().getTelefoneResidencial();
			telefoneTrabalho  		= matricula.getSegurado().getPessoaFisica().getTelefoneDoTrabalho();
			celular  				= matricula.getSegurado().getPessoaFisica().getCelular();
			email  					= matricula.getSegurado().getPessoaFisica().getEmail();
			estadoCivil  			= getEstadoCivil(matricula.getSegurado().getPessoaFisica().getEstadoCivil());
			municipio 				= matricula.getSegurado().getPessoaFisica().getEndereco().getMunicipio();
			cidade					= municipio==null?"":municipio.getDescricao();
			bairro					= matricula.getSegurado().getPessoaFisica().getEndereco().getBairro();
			
			//CAMPOS FERNANDO
			logradouro 	= matricula.getSegurado().getPessoaFisica().getEndereco().getLogradouro();
			numero 		= matricula.getSegurado().getPessoaFisica().getEndereco().getNumero();
			complemento =  matricula.getSegurado().getPessoaFisica().getEndereco().getComplemento();
			cep 		= matricula.getSegurado().getPessoaFisica().getEndereco().getCep();
			uf 			= municipio==null?"":municipio.getEstado().getUf();
			
			if (matricula.getEmpresa() != null) {
				idGrupo  			= getIdGrupo(matricula.getEmpresa().getIdGrupo());
			} else {
				idGrupo 			= "";
			}
			recadastrado 			= matricula.getSegurado().isRecadastrado()?"Sim":"Não";
			situacao 				= matricula.getSegurado().getSituacao().getDescricao();
			
			csvLine.add((Utils.isStringVazia(idSegurado) ? "" : idSegurado));
			csvLine.add((Utils.isStringVazia(numeroDoCartao) ? "" : numeroDoCartao));
			csvLine.add((Utils.isStringVazia(matricula_) ? "" : matricula_));
			csvLine.add((Utils.isStringVazia(sexo) ? "" : sexo));
			csvLine.add((Utils.isStringVazia(nome) ? "" : nome));
			csvLine.add((Utils.isStringVazia(nomeDaMae) ? "" : nomeDaMae));
			csvLine.add((Utils.isStringVazia(dataNascimento) ? "" : dataNascimento));
			csvLine.add((Utils.isStringVazia(identidade) ? "" : identidade));
			csvLine.add((Utils.isStringVazia(cpf) ? "" : cpf));
			csvLine.add((Utils.isStringVazia(telefoneResidencia) ? "" : telefoneResidencia));
			csvLine.add((Utils.isStringVazia(telefoneTrabalho) ? "" : telefoneTrabalho));
			csvLine.add((Utils.isStringVazia(celular) ? "" : celular));
			csvLine.add((Utils.isStringVazia(email) ? "" : email));
			csvLine.add((Utils.isStringVazia(estadoCivil) ? "" : estadoCivil));
			csvLine.add((Utils.isStringVazia(idGrupo) ? "" : idGrupo));
			csvLine.add(viaDoCartao);
			csvLine.add((Utils.isStringVazia(recadastrado) ? "" : recadastrado));
			csvLine.add((Utils.isStringVazia(situacao) ? "" : situacao));
			csvLine.add((matricula.getSegurado().getDataAdesao() == null ? "" : dataFormat.format(matricula.getSegurado().getDataAdesao())));
			csvLine.add((Utils.isStringVazia(cidade) ? "" : cidade));
			csvLine.add((Utils.isStringVazia(bairro) ? "" : bairro));
			
			csvLine.add((Utils.isStringVazia(logradouro) ? "" : logradouro));
			csvLine.add((Utils.isStringVazia(numero) ? "" : numero));
			csvLine.add((Utils.isStringVazia(complemento) ? "" : complemento));
			csvLine.add((Utils.isStringVazia(cep) ? "" : cep));
			csvLine.add((Utils.isStringVazia(uf) ? "" : uf));
			
			csvLinesTitulares.add(csvLine);
		}
		
		for (List<String> line : csvLinesTitulares) {
			bufTitulares.append(makeCSVLine(line, ';', false, true));
		}
		
		return bufTitulares.toString().getBytes();
	}
	
	public byte[] gerarCSVDependentes(List<DependenteInterface> dependentes) throws Exception {
		StringBuffer bufDependentes = new StringBuffer();
		List<List<String>> csvLinesDependentes = new ArrayList<List<String>>(dependentes.size());
		List<String> csvLine;
		String viaDoCartao = "1";
		
		String idDependente, idSegurado, numeroDoCartao, sexo, nome, dataNascimento;
		String identidade, cpf, recadastrado, situacao, descricao, ordem, bairro, cidade;
		MunicipioInterface municipio;
		String logradouro, numero, complemento, cep, uf;
		
		csvLine = new ArrayList<String>(15);
		csvLine.add("ID Dependente");
		csvLine.add("Número do Cartão");
		csvLine.add("Sexo");
		csvLine.add("Nome");
		csvLine.add("Data de Nascimento");
		csvLine.add("Identidade");
		csvLine.add("CPF");
		csvLine.add("Descrição");
		csvLine.add("ID Titular");
		csvLine.add("Via do Cartão");
		csvLine.add("Recadastrado?");
		csvLine.add("Situação");
		csvLine.add("Ordem");
		csvLine.add("Data de Adesão");
		csvLine.add("Cidade");
		csvLine.add("Bairro");
		
		csvLine.add("Logradouro");
		csvLine.add("Numero");
		csvLine.add("Complemento");
		csvLine.add("CEP");
		csvLine.add("UF");
		
		csvLinesDependentes.add(csvLine);
		
		for (DependenteInterface dependente : dependentes) {
			
			if(dependente.getTitular() != null){
				csvLine = new ArrayList<String>(15);
				
				idDependente  			= String.valueOf(dependente.getIdSegurado());
				numeroDoCartao  		= dependente.getNumeroDoCartao();
				sexo  					= getSexoString(dependente.getPessoaFisica().getSexo());
				nome  					= dependente.getPessoaFisica().getNome();
				dataNascimento  		= getDataNascimento(dependente.getPessoaFisica().getDataNascimento());
				identidade  			= dependente.getPessoaFisica().getIdentidade();
				cpf  					= dependente.getPessoaFisica().getCpf();
				descricao 				= dependente.getDescricaoDaDependencia();
				idSegurado  			= String.valueOf(dependente.getTitular().getIdSegurado());
				recadastrado 			= dependente.isRecadastrado()? "Sim":"Não";
				situacao 				= dependente.getSituacao().getDescricao();
				ordem					= String.valueOf(dependente.getOrdem());
				municipio 				= dependente.getTitular().getPessoaFisica().getEndereco().getMunicipio();
				cidade					= municipio==null?"":municipio.getDescricao();
				bairro					= dependente.getTitular().getPessoaFisica().getEndereco().getBairro();
				
				//CAMPOS FERNANDO
				logradouro 	= dependente.getTitular().getPessoaFisica().getEndereco().getLogradouro();
				numero 		= dependente.getTitular().getPessoaFisica().getEndereco().getNumero();
				complemento = dependente.getTitular().getPessoaFisica().getEndereco().getComplemento();
				cep 		= dependente.getTitular().getPessoaFisica().getEndereco().getCep();
				uf 			= municipio==null ? "" : municipio.getEstado().getUf();
				
				csvLine.add((Utils.isStringVazia(idDependente) ? "" : idDependente));
				csvLine.add((Utils.isStringVazia(numeroDoCartao) ? "" : numeroDoCartao));
				csvLine.add((Utils.isStringVazia(sexo) ? "" : sexo));
				csvLine.add((Utils.isStringVazia(nome) ? "" : nome));
				csvLine.add((Utils.isStringVazia(dataNascimento) ? "" : dataNascimento));
				csvLine.add((Utils.isStringVazia(identidade) ? "" : identidade));
				csvLine.add((Utils.isStringVazia(cpf) ? "" : cpf));
				csvLine.add((Utils.isStringVazia(descricao) ? "" : descricao));
				csvLine.add((Utils.isStringVazia(idSegurado) ? "" : idSegurado));
				csvLine.add(viaDoCartao);
				csvLine.add((Utils.isStringVazia(recadastrado) ? "" : recadastrado));
				csvLine.add((Utils.isStringVazia(situacao) ? "" : situacao));
				csvLine.add((Utils.isStringVazia(ordem) ? "" : ordem));
				csvLine.add((dependente.getDataAdesao() == null ? "" : dataFormat.format(dependente.getDataAdesao())));
				csvLine.add((Utils.isStringVazia(cidade) ? "" : cidade));
				csvLine.add((Utils.isStringVazia(bairro) ? "" : bairro));
				
				csvLine.add((Utils.isStringVazia(logradouro) ? "" : logradouro));
				csvLine.add((Utils.isStringVazia(numero) ? "" : numero));
				csvLine.add((Utils.isStringVazia(complemento) ? "" : complemento));
				csvLine.add((Utils.isStringVazia(cep) ? "" : cep));
				csvLine.add((Utils.isStringVazia(uf) ? "" : uf));
				
				csvLinesDependentes.add(csvLine);
			
			}
		}
		
		for (List<String> line : csvLinesDependentes) {
			bufDependentes.append(makeCSVLine(line, ';', false, true));
		}
		
		
		return bufDependentes.toString().getBytes();
	}
	
	public byte[] gerarCSVGrupos(List<GrupoBC> grupos) throws Exception {
		StringBuffer bufGrupos = new StringBuffer();
		List<List<String>> csvLinesGrupos = new ArrayList<List<String>>(grupos.size());
		List<String> csvLine;
		String idGrupo, codigoLegado, descricao;
		
		csvLine = new ArrayList<String>(3);
		csvLine.add("ID Grupo");
		csvLine.add("Codigo Legado");
		csvLine.add("Descricao");
		csvLinesGrupos.add(csvLine);
		
		for (GrupoBC grupo : grupos) {
			csvLine = new ArrayList<String>(3);
			  
			idGrupo 				= String.valueOf(grupo.getIdGrupo());
			codigoLegado 			= grupo.getCodigoLegado();
			descricao 				= grupo.getDescricao();
			
			csvLine.add((Utils.isStringVazia(idGrupo) ? "" : idGrupo));
			csvLine.add((Utils.isStringVazia(codigoLegado) ? "" : codigoLegado));
			csvLine.add((Utils.isStringVazia(descricao) ? "" : descricao));
			
			csvLinesGrupos.add(csvLine);
		}
		
		for (List<String> line : csvLinesGrupos) {
			bufGrupos.append(makeCSVLine(line, ';', false, true));
		}
		
		return bufGrupos.toString().getBytes();
	}
	
}
