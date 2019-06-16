package fr.d2factory.libraryapp.member;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;

public class ResidentMember extends Member {

	public ResidentMember(BigDecimal initialWallet) {
		super(initialWallet);
	}

	@Override
	public void payBook(int numberOfDays) {
		if (numberOfDays <= 60) {
			wallet = wallet.subtract(new BigDecimal(String.valueOf(0.1 * numberOfDays)));
		} else {
			wallet = wallet.subtract(new BigDecimal(String.valueOf(0.1 * 60 + 0.2 * (numberOfDays - 60))));
		}

	}

	@Override
	public boolean isLate(LocalDate borrowingDate) {
		return (int) Duration.between(borrowingDate.atStartOfDay(), LocalDate.now().atStartOfDay()).toDays() > 60;
	}

}
