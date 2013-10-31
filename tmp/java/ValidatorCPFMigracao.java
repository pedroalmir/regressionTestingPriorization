package br.com.infowaypi.ecare.services.cadastros;

import org.hibernate.FlushMode;

import br.com.infowaypi.ecare.segurados.Dependente;
import br.com.infowaypi.ecare.segurados.DependenteSuplementar;
import br.com.infowaypi.ecare.segurados.Pensionista;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Utils;

public class ValidatorCPFMigracao {
	
	public void validate(Dependente dependente, String cpf, Integer tipoBeneficiario) throws ValidateException{
		
		boolean isDependenteMaiorQue16 = dependente.getIdade() > 16;
		boolean isCPFInformado = !Utils.isStringVazia(cpf);
		boolean isBeneficiarioPensionsita = tipoBeneficiario.equals(MigrarDependentes.TIPO_PENSIONISTA);
		
		if(isDependenteMaiorQue16 && !isCPFInformado) {
			throw new ValidateException("Caro usuário, o dependente escolhido tem idade maior que 16. O preenchimento do campo CPF é obrigatório.");
		}
		
		if(isBeneficiarioPensionsita && !isCPFInformado)
			throw new ValidateException("Caro usuário, o preenchimento do campo CPF é obrigatório ao migrar qualquer tipo de dependente para Pensionista.");
		
		if(dependente.getIdade() > 16 && !Utils.isCpfValido(cpf))
			throw new  ValidateException("Caro Usuário, o CPF informado é inválido.");
		
		HibernateUtil.currentSession().setFlushMode(FlushMode.COMMIT);
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("pessoaFisica.cpf",cpf));
		
		Integer countCPF = 0;
		if(isBeneficiarioPensionsita)
			countCPF = sa.resultCount(Pensionista.class);
		else countCPF = sa.resultCount(DependenteSuplementar.class);
		
		if(countCPF>0)
			throw new RuntimeException("Caro usuário, já existe um "+(isBeneficiarioPensionsita?"PENSIONISTA":"DEPENDENTE SUPLEMENTAR")+" cadastrado com o CPF "+cpf+".");
		
	}


}
