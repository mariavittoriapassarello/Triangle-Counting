package gebd.progetto;

import java.io.Serializable;
import java.util.ArrayList;

public class ArcoGradi implements Serializable{

	
	public ArcoGradi() {
		
	}
	
	public ArcoGradi(String idEntrata, String idUscita, String degreeEntrata, String degreeUscita) {
		super();
		this.idEntrata = idEntrata;
		this.idUscita = idUscita;
		this.degreeEntrata = degreeEntrata;
		this.degreeUscita = degreeUscita;
	}
	public ArcoGradi(String idEntrata, String idUscita) {
		super();
		this.idEntrata = idEntrata;
		this.idUscita = idUscita;
	}
	public ArcoGradi(String id, ArrayList<String> infoGammaPiu) {
		super();
		this.id = id;
		this. infoGammaPiu = infoGammaPiu;
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
	String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ArrayList<String> getInfoGammaPiu() {
		return infoGammaPiu;
	}

	public void setInfoGammaPiu(ArrayList<String> infoGammaPiu) {
		this.infoGammaPiu = infoGammaPiu;
	}
	ArrayList<String> infoGammaPiu;


}
