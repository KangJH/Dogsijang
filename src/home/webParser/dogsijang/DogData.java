package home.webParser.dogsijang;

import java.io.Serializable;


public class DogData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int		iNo;
	String	strSpecies;
	String	strCharacter;
	String	strPrice;
	String	strUri;
	String 	strContactNum;
	boolean blReadMark;
	
	public DogData() {
		iNo = -1;
		strSpecies = null;
		strCharacter = null;
		strPrice = null;
		strUri = null;
		strContactNum = null;
		blReadMark = false;
	}
	public boolean equals(DogData input) {
		boolean retVal = false;
		if(strSpecies != null && strCharacter != null && strPrice != null && strContactNum != null &&
				input != null && input.strSpecies != null && input.strCharacter != null && input.strPrice != null) {
			if(strSpecies.equals(input.strSpecies) && strCharacter.equals(input.strCharacter) && 
					strPrice.equals(input.strPrice) && strContactNum.equals(strContactNum) ) {
				retVal = true;
			}
		}
		return retVal;
	}
}
