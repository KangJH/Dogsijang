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
		if(strSpecies != null && iNo > 0 &&
				input != null && input.strSpecies != null && input.iNo > 0 ) {
			if(iNo == input.iNo && strSpecies.equals(input.strSpecies)) {
				retVal = true;
			}
		}
		return retVal;
	}
}
