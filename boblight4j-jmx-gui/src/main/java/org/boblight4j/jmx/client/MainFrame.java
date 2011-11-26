package org.boblight4j.jmx.client;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.InstanceNotFoundException;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;
import org.boblight4j.jmx.client.CurveEditorPanel.Model;
import org.boblight4j.server.config.IPoint;
import org.boblight4j.server.config.LightAccessorMBean;
import org.boblight4j.server.config.Point;

public class MainFrame extends JFrame {

	private final class ComboBoxListener implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {

			final Model data = MainFrame.this.curveEditorPanel.getData();

			final String lightName = (String) ((JComboBox) e.getSource())
					.getSelectedItem();
			data.lightName = lightName;

			LOG.info("Light " + lightName + " selected");

			try
			{
				final ObjectInstance objectInstance = mbsc
						.getObjectInstance(new ObjectName(
								"org.boblight.server.config", "type", "Light ["
										+ lightName + "]"));

				final LightAccessorMBean newMXBeanProxy = JMX.newMBeanProxy(
						mbsc, objectInstance.getObjectName(),
						LightAccessorMBean.class);

				final Map<String, IPoint[]> colorAdjustments = newMXBeanProxy
						.getColorAdjustments();

				final IPoint[] iPoints = colorAdjustments.get(data.lightName);

				data.points.clear();

				if (iPoints != null)
				{
					LOG.info("Adding " + iPoints.length + " points");
					for (final IPoint iPoint : iPoints)
					{
						data.points.add(iPoint);
					}
				}

				MainFrame.this.curveEditorPanel.repaint();

			}
			catch (final InstanceNotFoundException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			catch (final MalformedObjectNameException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			catch (final NullPointerException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			catch (final IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
	}

	private static final Logger LOG = Logger
			.getLogger(MainFrame.ComboBoxListener.class);

	private static MBeanServerConnection mbsc;

	/**
	 * 
	 */
	private static final long serialVersionUID = -892658287417724755L;

	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) {

		try
		{
			final JMXServiceURL url = new JMXServiceURL(
					"service:jmx:rmi:///jndi/rmi://zbox.local:9000/jmxrmi");
			final JMXConnector jmxc = JMXConnectorFactory.connect(url, null);

			mbsc = jmxc.getMBeanServerConnection();

			final Set<ObjectInstance> queryMBeans = mbsc
					.queryMBeans(null, null);

			final List<String> lightNames = new ArrayList<String>();
			for (final Iterator<ObjectInstance> iterator = queryMBeans
					.iterator(); iterator.hasNext();)
			{
				final ObjectInstance objectInstance = (ObjectInstance) iterator
						.next();

				final String className = objectInstance.getClassName();
				if (!className
						.equals("org.boblight4j.server.config.LightAccessor"))
				{
					iterator.remove();
					continue;
				}

				final ObjectName objectName = objectInstance.getObjectName();

				// Light [<name>]
				final String keyProperty = objectName.getKeyProperty("type");

				final String lightName = keyProperty.substring(7,
						keyProperty.length() - 1);
				lightNames.add(lightName);
			}

			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					try
					{

						final MainFrame frame = new MainFrame(queryMBeans,
								lightNames.toArray(new String[] {}));
						frame.setVisible(true);
					}
					catch (final Exception e)
					{
						e.printStackTrace();
					}
				}
			});

		}
		catch (final MalformedURLException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch (final IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private final ColorChoosePanel colorChooser;
	private final JComboBox comboBox;
	private final CurveEditorPanel curveEditorPanel;

	protected Set<ObjectInstance> queryMBeans;

	/**
	 * Create the frame.
	 * 
	 * @param changeListener
	 *            TODO
	 * @param lightNames
	 *            TODO
	 */
	public MainFrame(final Set<ObjectInstance> queryMBeans,
			final String[] lightNames) {

		final ChangeListener changeListener = new ChangeListener() {

			@Override
			public void stateChanged(final ChangeEvent e) {

				for (final Object element : queryMBeans)
				{
					final ObjectInstance objectInstance = (ObjectInstance) element;

					final ObjectName objectName = objectInstance
							.getObjectName();
					final String keyProperty = objectName
							.getKeyProperty("type");
					final Model model = (Model) e.getSource();
					if (keyProperty.equals("Light [" + model.lightName + "]"))
					{

						final LightAccessorMBean newMXBeanProxy = JMX
								.newMBeanProxy(mbsc, objectName,
										LightAccessorMBean.class);

						newMXBeanProxy.setColorAdjustments(model.colorIndex,
								model.points.toArray(new Point[] {}));
						break;
					}

				}

			}
		};

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(100, 100, 450, 300);

		final JPanel contentPane = new JPanel();

		this.setContentPane(contentPane);

		this.curveEditorPanel = LightPanelFactory
				.createCurvePanel(changeListener);
		this.colorChooser = LightPanelFactory.createColorChooser(new Object[] {
				"red", "green", "blue" }, this.curveEditorPanel);

		final JPanel lightPanel = LightPanelFactory.createLightPanel(
				this.curveEditorPanel, this.colorChooser);

		final JPanel panel = new JPanel();
		final GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(gl_contentPane
				.createParallelGroup(Alignment.LEADING)
				.addComponent(panel, GroupLayout.DEFAULT_SIZE, 448,
						Short.MAX_VALUE)
				.addGroup(
						gl_contentPane
								.createSequentialGroup()
								.addGap(12)
								.addComponent(lightPanel,
										GroupLayout.DEFAULT_SIZE, 424,
										Short.MAX_VALUE).addContainerGap()));
		gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(
				Alignment.LEADING).addGroup(
				gl_contentPane
						.createSequentialGroup()
						.addComponent(panel, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(lightPanel, GroupLayout.DEFAULT_SIZE,
								206, Short.MAX_VALUE).addContainerGap()));

		this.comboBox = new JComboBox();

		this.comboBox.setModel(new DefaultComboBoxModel(lightNames));
		this.comboBox.addActionListener(new ComboBoxListener());
		final GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(
				Alignment.LEADING).addGroup(
				gl_panel.createSequentialGroup().addContainerGap()
						.addComponent(this.comboBox, 0, 424, Short.MAX_VALUE)
						.addContainerGap()));
		gl_panel.setVerticalGroup(gl_panel.createParallelGroup(
				Alignment.LEADING).addGroup(
				gl_panel.createSequentialGroup()
						.addContainerGap()
						.addComponent(this.comboBox,
								GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addContainerGap(GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)));
		panel.setLayout(gl_panel);
		contentPane.setLayout(gl_contentPane);

		contentPane.add(lightPanel);

	}
}
