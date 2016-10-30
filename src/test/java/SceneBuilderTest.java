import unknow.scene.builder.*;

import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl3.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.kotcrab.vis.ui.*;

public class SceneBuilderTest implements ApplicationListener
	{
	public static void main(String[] arg) throws Exception
		{
		Lwjgl3ApplicationConfiguration conf=new Lwjgl3ApplicationConfiguration();
		conf.setTitle("Game");
		conf.setWindowedMode(560, 368);
		conf.setResizable(true);
		new Lwjgl3Application(new SceneBuilderTest(), conf);
		}

	private Stage stage;

	@Override
	public void create()
		{
		VisUI.load();
		stage=new Stage();
		Gdx.input.setInputProcessor(stage);
		// @formatter:off
		String str=
				"<table name='root' fillParent='true' setDebug='true'>\n"
				+ "	<row left=''>\n"
				+ "		<menuBar>"
				+ "			<menu name='1'>"
				+ "				<menuItem name='s1'/>"
				+ "				<menuItem name='Quit'>"
				+ "					<listener name='quit'/>"
				+ "				</menuItem>"
				+ "			</menu>\n"
				+ "			<menu name='2'>"
				+ "				<menuItem name='t2'/>"
				+ "			</menu>"
				+ "		</menuBar>"
				+ "	</row>"
				+ "	<row expand=''>\n"
				+ "		<actor class='com.kotcrab.vis.ui.widget.VisLabel' text='test'/>\n"
				+ "	</row>\n"
				+ "</table>\n";
		// @formatter:on
		try
			{
			SceneBuilder builder=new SceneBuilder();
			builder.addListener("quit", new ChangeListener()
				{
					@Override
					public void changed(ChangeEvent event, Actor actor)
						{
						Gdx.app.exit();
						}
				});
			Actor build=(Actor)builder.build(str);
			stage.addActor(build);
			System.out.println("done: "+build);
			}
		catch (Exception e)
			{
			e.printStackTrace();
			}
		}

	@Override
	public void resize(int width, int height)
		{
		stage.getViewport().update(width, height, true);
		}

	@Override
	public void render()
		{
		stage.getViewport().apply();
		stage.act();
		stage.draw();
		}

	@Override
	public void pause()
		{
		}

	@Override
	public void resume()
		{
		}

	@Override
	public void dispose()
		{
		}
	}
