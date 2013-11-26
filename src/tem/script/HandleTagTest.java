package tem.script;

public class HandleTagTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String tags1 = "<mysql><sql-server><oracle><database-design><timezone>";
		String tags2 = "<android>";
		String[] tags = tags1.replaceAll("[<>]", " ").split("  ");
		System.out.println(tags.length);
		
		for(String tag : tags){
			System.out.println(tag.replace(" ", ""));
		}
	}

}
