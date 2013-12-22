package home.webParser.dogsijang;

public class DogData {
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
		if(strSpecies != null && strCharacter != null && strPrice != null &&
				input != null && input.strSpecies != null && input.strCharacter != null && input.strPrice != null) {
			if(iNo == input.iNo && strSpecies.equals(input.strSpecies) &&
					strCharacter.equals(input.strCharacter) && strPrice.equals(input.strPrice) ) {
				retVal = true;
			}
		}
		return retVal;
	}
}
