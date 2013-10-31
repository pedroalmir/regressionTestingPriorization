/* 
 * Criado em 07/07/2006 - 16:17:45
 */
package br.com.infowaypi.ecarebc.service;

public enum Agente {
	PRESTADOR {
		@Override
		public boolean isFromPrestador() {
			return false;
		}
	},
	MARCADOR , 
	ROOT{
		public boolean isEspecial() {
			return true;
		}
		
	},
	SUPERVISOR {
		public boolean isEspecial() {
			return true;
		}
	};

	public boolean isEspecial() {
		return false;
	}
	
	public boolean isFromPrestador() {
		return true; 
	}
}