package br.com.infowaypi.ecare.services.questionarioqualificado;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.hibernate.FlushMode;

import br.com.infowaypi.ecare.arquivos.ArquivoDownloadJPG;
import br.com.infowaypi.ecare.questionarioqualificado.ManagerPerguntas;
import br.com.infowaypi.ecare.questionarioqualificado.Questionario;
import br.com.infowaypi.ecare.questionarioqualificado.Resposta;
import br.com.infowaypi.ecare.questionarioqualificado.validators.ValidatorTipoArquivoJPG;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.services.BuscarSegurados;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

/**
 * @author DANNYLVAN
 *
 * Fluxo para aplicação do questionário qualificado
 */
public class FlowAplicarQuestionarioQualificado {
	
	public Segurado buscarSegurado(String cpf, String cartao, boolean isAlteracao) throws ValidateException{
		HibernateUtil.currentSession().setFlushMode(FlushMode.COMMIT);
		Segurado segurado = (Segurado) BuscarSegurados.getSegurado(cartao, cpf, Segurado.class, true);
		Questionario questionario = segurado.getQuestionario();
		
		if(questionario == null){
			questionario = new Questionario(segurado);
			segurado.setQuestionario(questionario);
		}
		
		boolean semArquivo = questionario.getArquivos().isEmpty();
		if(isAlteracao){
			Assert.isFalse(semArquivo, "Ainda não foi aplicado questionário para este segurado, portanto não é possível alterá-lo.");
		} else {
			Assert.isTrue(semArquivo, "Já foi aplicado questionário para este segurado.");
			List<Resposta> respostasQuestionario = ManagerPerguntas.getRespostasQuestionario();
			questionario.setRespostas(respostasQuestionario);
		}
		
		tocarQuestionario(questionario);
		return segurado;
	}
	
	public Questionario preencherQuestionario(Questionario questionario, Segurado segurado) throws ValidateException {
		
		questionario.validarPreenchimento();
		
		questionario.setSegurado(segurado);
		
		return questionario;
	}
	
	public Questionario importarArquivo(String motivo, Date dataAplicacao, 
			Questionario questionario, byte[] file, byte[] file2, UsuarioInterface usuario) throws Exception {
		
		this.validarDataAlteracao(questionario, dataAplicacao);
		
		if(!questionario.getArquivos().isEmpty() && Utils.isStringVazia(motivo)){
			throw new ValidateException("O questionário já foi aplicado, por favor, informe o motivo de alteração.");
		}
		
		if(Utils.isStringVazia(motivo)){
			motivo = "Aplicação do Questionário Qualificado";
		}
		
		List<ArquivoDownloadJPG> arquivos = new ArrayList<ArquivoDownloadJPG>();
		
		if(file != null){
			uploadAquivo(arquivos, file, "Parte1");
		}
		
		if(file2 != null){
			uploadAquivo(arquivos, file2,"Parte2");
		}
		
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		ZipOutputStream zipFile = new ZipOutputStream(bytes);
		zipFile.setLevel(9);
		for (ArquivoDownloadJPG arq : arquivos) {
			zipFile.putNextEntry(new ZipEntry(arq.getDescricaoArquivo()));
			zipFile.write(arq.getArquivo());
		}
		
		ArquivoDownloadJPG arquivo = null;
		if (!arquivos.isEmpty()) {
			zipFile.finish();
			
			arquivo = new ArquivoDownloadJPG();
			arquivo.setArquivo(bytes.toByteArray());
			arquivo.setDataUpload(new Date());
			arquivo.setDescricaoArquivo("questionario_"+Utils.format(new Date(), "ddMMyyyyHHmm"));
		}
		
		if(arquivo != null){
			questionario.getArquivos().add(arquivo);
		}
		
		if(questionario.getDataCriacao() == null){
			if (file == null){
				throw new ValidateException("Na aplicação do questionário qualificado é necessário informar o arquivo digitalizado no formato 'jgp'.");
			}
			questionario.setDataCriacao(new Date());
		}
		
		questionario.setDataAlteracao(dataAplicacao);
		questionario.setUsuario(usuario);
		questionario.getAlteracoesDoQuestionario().adicionarAlteracao(motivo, usuario, dataAplicacao);
		
		ImplDAO.save(questionario);
		
		return questionario; 
	}

	private void uploadAquivo(List<ArquivoDownloadJPG> arquivos, byte[] file, String complemento) throws ValidateException {
		ValidatorTipoArquivoJPG.isArquivoJPG(file);
		ArquivoDownloadJPG arquivo = new ArquivoDownloadJPG();
		arquivo.setArquivo(file);
		arquivo.setDescricaoQuestionario(complemento);
		arquivo.setDataUpload(new Date());
		arquivos.add(arquivo);
	}

	/**
	 *  Valida a data de aplicação do questionário deve ser maior que a ultima alteração
	 */
	private void validarDataAlteracao(Questionario questionario, Date dataAplicacao) throws ValidateException{
		Date ultimaAlteracao = questionario.getDataAlteracao();
		
		if (ultimaAlteracao != null && (Utils.compareData(dataAplicacao, ultimaAlteracao) < 0)){
			throw new ValidateException("A data da aplicação do questionário deve ser superior à data da ultima alteração.");
		}
	}
	
	private void tocarQuestionario(Questionario questionario) {
		questionario.getCids().size();
		questionario.getSubgruposProcedimentos().size();
		
		if(questionario.getAlteracoesDoQuestionario() != null){
			questionario.getAlteracoesDoQuestionario().getAlteracoes().size();
		}
		if(questionario.getUsuario() != null){
			questionario.getUsuario().getNome();
		}
	}
	
}
