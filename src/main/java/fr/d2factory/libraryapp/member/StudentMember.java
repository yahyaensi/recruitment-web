package fr.d2factory.libraryapp.member;

import java.math.BigDecimal;

public class StudentMember extends Member {
	
	private boolean isFirstYear;
	
	public StudentMember(boolean isFirstYear, BigDecimal initialWallet) {
		super(initialWallet);
		this.isFirstYear = isFirstYear;
	}

	@Override
	public void payBook(int numberOfDays) {
		if (numberOfDays <= 30) {
			if (isFirstYear) {
				wallet = wallet.subtract(new BigDecimal(String.valueOf(0.1 * (numberOfDays - 15))));
			}
		}
		
	}

}
