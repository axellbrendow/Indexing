
public class Main
{

	public static void main(String[] args)
	{
		HashDinamica<entity, entity> hash = new HashDinamica<>(
			"diretorio.dir",
			"buckets.db",
			3,
			Integer.BYTES + 300,
			Integer.BYTES + 300,
			entity.class.getConstructors(),
			entity.class.getConstructors(),
			funcaoHash
		);
	}

}
