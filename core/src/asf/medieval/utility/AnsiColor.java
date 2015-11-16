package asf.medieval.utility;

/**
 * Created by daniel on 11/15/15.
 */
public enum AnsiColor {

	RESET("\u001B[0m"),
	//
	BLACK("\u001B[2;30m"),
	RED("\u001B[2;31m"),
	GREEN("\u001B[2;32m"),
	YELLOW("\u001B[2;33m"),
	BLUE("\u001B[2;34m"),
	PURPLE("\u001B[2;35m"),
	MAGENTA("\u001B[2;35m"),
	CYAN("\u001B[2;36m"),
	WHITE("\u001B[2;37m"),
	//
	BLACK1("\u001B[3;30m"),
	RED1("\u001B[3;31m"),
	GREEN1("\u001B[3;32m"),
	YELLOW1("\u001B[3;33m"),
	BLUE1("\u001B[3;34m"),
	MAGENTA1("\u001B[3;35m"),
	CYAN1("\u001B[3;36m"),
	WHITE1("\u001B[3;37m"),
	//
	BLACK2("\u001B[0;30m"),
	RED2("\u001B[0;31m"),
	GREEN2("\u001B[0;32m"),
	YELLOW2("\u001B[0;33m"),
	BLUE2("\u001B[0;34m"),
	MAGENTA2("\u001B[0;35m"),
	CYAN2("\u001B[0;36m"),
	WHITE2("\u001B[0;37m"),
	//
	BLACK3("\u001B[1;30m"),
	RED3("\u001B[1;31m"),
	GREEN3("\u001B[1;32m"),
	YELLOW3("\u001B[1;33m"),
	BLUE3("\u001B[1;34m"),
	MAGENTA3("\u001B[1;35m"),
	CYAN3("\u001B[1;36m"),
	WHITE3("\u001B[1;37m");
	//
      /*
      BACKGROUND_BLACK("\u001B[40m"),
      BACKGROUND_RED("\u001B[41m"),
      BACKGROUND_GREEN("\u001B[42m"),
      BACKGROUND_YELLOW("\u001B[43m"),
      BACKGROUND_BLUE("\u001B[44m"),
      BACKGROUND_MAGENTA("\u001B[45m"),
      BACKGROUND_CYAN("\u001B[46m"),
      BACKGROUND_WHITE("\u001B[47m");
      */
	private String colorCode;

	AnsiColor(String colorCode) {
		this.colorCode = colorCode;
	}

	public String getColorName() {
		String[] split = super.toString().toLowerCase().split("_");
		if (split.length == 1) {
			return split[0].substring(0, 1).toUpperCase() + split[0].substring(1);
		} else if (split.length == 2) {
			return split[0].substring(0, 1).toUpperCase() + split[0].substring(1) + " "
				+ split[1].substring(0, 1).toUpperCase() + split[1].substring(1);
		} else {
			String colorName = super.toString().toLowerCase();
			return colorName.substring(0, 1).toUpperCase() + colorName.substring(1);
		}
	}

	public String getColorCode() {
		return colorCode;
	}

	/**
	 *
	 * returns the colorCode rather than the name of the enum.
	 *
	 * use getColorName() if you want the colorname.
	 *
	 * @return
	 */
	@Override
	public String toString() {
		return colorCode;
	}

	public static void printColorInformation() {
		for (AnsiColor c : AnsiColor.values()) {
			System.out.println(c.getColorCode()+c.getColorName()+AnsiColor.RESET);
		}
	}
}
