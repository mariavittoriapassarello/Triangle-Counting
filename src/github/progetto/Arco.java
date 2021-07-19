package github.progetto;

import java.io.Serializable;

public class Arco implements Serializable{

	public Arco() {
	}
	
	public Arco(String idEntrata, String idUscita, String degreeEntrata, String degreeUscita) {
		super();
		this.idEntrata = idEntrata;
		this.idUscita = idUscita;
		this.degreeEntrata = degreeEntrata;
		this.degreeUscita = degreeUscita;
	}
	public Arco(String idEntrata, String idUscita) {
		super();
		this.idEntrata = idEntrata;
		this.idUscita = idUscita;
	}
	public String getIdEntrata() {
		return idEntrata;
	}

	public void setIdEntrata(String idEntrata) {
		this.idEntrata = idEntrata;
	}

	public String getIdUscita() {
		return idUscita;
	}

	public void setIdUscita(String idUscita) {
		this.idUscita = idUscita;
	}

	public String getDegreeEntrata() {
		return degreeEntrata;
	}

	public void setDegreeEntrata(String degreeEntrata) {
		this.degreeEntrata = degreeEntrata;
	}

	public String getDegreeUscita() {
		return degreeUscita;
	}

	public void setDegreeUscita(String degreeUscita) {
		this.degreeUscita = degreeUscita;
	}
	String idEntrata;
	String idUscita;
	String degreeEntrata;
	String degreeUscita;

}
