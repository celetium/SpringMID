package b.SpringMID.adapter;

public interface Constants {
	public static final int TRIM_RIGHT_WHEN_PARSING = 1;
	public static final int TRIM_LEFT_WHEN_PARSING = 2;
	public static final int TRIM_RIGHT_WHEN_RENDERING = 4;
	public static final int TRIM_LEFT_WHEN_RENDERING = 8;
	public static final int REF_AS_MACRO = 0;
	public static final int REF_AS_CHILDREN = 1;
	public static final int REF_AS_SIBLING = 2;
	public static final int USE_FOR_PARSING = 1; // 用于iBinary, iOption
	public static final int USE_FOR_RENDERING = 2; // 用于iBinary, iOption
}
