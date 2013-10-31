/**
 * 
 */
package br.com.infowaypi.ecare.arquivos;

import org.apache.commons.lang.StringUtils;

import br.com.infowaypi.ecare.segurados.Empresa;
import br.com.infowaypi.ecare.segurados.Matricula;
import br.com.infowaypi.msr.utils.Utils;

/**
 * @author Marcus
 *
 */
public class ArquivoDeEntradaDeLancamentos {
	private Empresa empresa;
	private StringBuffer buffer;
	private int numeroRegistros;
	
	
	public ArquivoDeEntradaDeLancamentos(Empresa empresa) {
		this.buffer = new StringBuffer();
		this.empresa = empresa;
		this.numeroRegistros = 0;
	}
	
	public void geraRegistroTipo1() {
		boolean nomeEmpresaOverFlow = false;
		String tipoRegistro = "1";
		String nomeDoLote = empresa.getDescricao();
		String nomeDoLoteTruncado = "";
		if(nomeDoLote.length() > 10) {
			nomeEmpresaOverFlow = true;
			nomeDoLoteTruncado = nomeDoLote.substring(0, 9);
		}else if (nomeDoLote.length() < 10) {
			nomeEmpresaOverFlow = true;
			nomeDoLoteTruncado = StringUtils.rightPad(nomeDoLote, 10, " ");
		}
		String filler1 = "    ";
		String codEmpresa = StringUtils.rightPad(empresa.getCodigoLegado(), 3, " ");
		String filler2 = "                     ";
		String filler3 = "000000000000000000000000000000000000000";
		
		buffer.append(tipoRegistro);
		if (nomeEmpresaOverFlow) {
			buffer.append(nomeDoLoteTruncado);
		}else {
			buffer.append(nomeDoLote);
		}
		buffer.append(filler1);
		buffer.append(codEmpresa);
		buffer.append(filler2);
		buffer.append(filler3);
		
		buffer.append(System.getProperty("line.separator"));
		
		++this.numeroRegistros;
	}
	
	public void geraRegistroTipo2() {
		for (Matricula matricula : empresa.getMatriculas()) {
			boolean nomeEmpresaOverFlow = false;
			String tipoRegistro = "2";
			String nomeDoLote = empresa.getDescricao();
			String nomeDoLoteTruncado = "";
			if(nomeDoLote.length() > 10) {
				nomeEmpresaOverFlow = true;
				nomeDoLoteTruncado = nomeDoLote.substring(0, 9);
			}else if (nomeDoLote.length() < 10) {
				nomeEmpresaOverFlow = true;
				nomeDoLoteTruncado = StringUtils.rightPad(nomeDoLote, 10, " ");
			}
			String estabelecimento = "0001";
			String codEmpresa = StringUtils.rightPad(empresa.getCodigoLegado(), 3," ");
			String prontuario = StringUtils.leftPad(matricula.getDescricao(), 10,"0");
			
			String dataCompetencia = "062007";
			String dataBase = "062007";
			String codigoVerba ="777";
			String valorDoLancamento = StringUtils.leftPad("1650", 17, "0");
			String referencia = StringUtils.leftPad("5", 11, "8");
			
			String calculo = "01";
			String quantidadeParcelas ="01";
			String Filler = "000";
			
			//TODO buffer;
			
			buffer.append(tipoRegistro);
			if (nomeEmpresaOverFlow) {
				buffer.append(nomeDoLoteTruncado);
			}else {
				buffer.append(nomeDoLote);
			}
			buffer.append(estabelecimento);
			buffer.append(codEmpresa);
			buffer.append(prontuario);
			buffer.append(dataCompetencia);
			buffer.append(dataBase);
			buffer.append(codigoVerba);
			buffer.append(valorDoLancamento);
			buffer.append(referencia);
			buffer.append(calculo);
			buffer.append(quantidadeParcelas);
			buffer.append(Filler);
			
			buffer.append(System.getProperty("line.separator"));
			
			++this.numeroRegistros;
		}
	}
	
	public void geraRegistroTipo9() {
		String tipoRegistro = "9";
		String filler = StringUtils.rightPad(" ", 70," ");
		++ this.numeroRegistros;
		String numeroRegistros = String.valueOf(this.numeroRegistros);
		
		this.buffer.append(tipoRegistro);
		this.buffer.append(filler);
		this.buffer.append(this.numeroRegistros);
	}
	
	public void geraArquivo() throws Exception {
		Utils.criarArquivo("C:/ArquivoDeLancamento.txt", "", this.buffer);
	}
}
