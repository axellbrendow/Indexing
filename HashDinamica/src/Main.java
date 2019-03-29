
public class Main
{

	public static void print(Object msg)
	{
		System.out.print(msg);
	}
	
	public static void println(Object msg)
	{
		print(msg + System.lineSeparator());
	}
	
	public static void main(String[] args)
	{
		entity ent1 = new entity(1, "entity 1");
		entity ent2 = new entity(2, "entity 2");
		entity ent3 = new entity(3, "entity 3");
		entity ent4 = new entity(4, "entity 4");
		entity ent5 = new entity(5, "entity 5");
		entity ent6 = new entity(6, "entity 6");
		
		try
		{
			HashDinamica<entity, entity> hash = new HashDinamica<entity, entity>(
				"diretorio.dir",
				"buckets.db",
				3,
				(short) (Integer.BYTES + 300),
				(short) (Integer.BYTES + 300),
				entity.class.getConstructor(),
				entity.class.getConstructor(),
				(ent) -> { return (int) ( Math.random() * 73 * ent.id ); }
			);
			
			hash.inserir(ent1, ent2);
			hash.inserir(ent1, ent3);
			hash.inserir(ent1, ent4);
			hash.inserir(ent5, ent6);
			
			println(hash.listarDadosComAChave(ent1).toString());
		}
		
		catch (NoSuchMethodException e)
		{
			e.printStackTrace();
		}
		
		catch (SecurityException e)
		{
			e.printStackTrace();
		}
	}

}
