package cs108javademoapp;

import com.csl.cs108javalibrary.CS108JavaLibrary;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.DefaultCaret;

public class CS108JavaDemoApp {
    JButton jbuttonScan, jbuttonConnect, jbuttonInventoryRfid, jbuttonInventoryBarcode, jbuttonSearchRfid, jbuttonReadWrite, jbuttonLockill;
    JLabel jlabelVersions, jlabelVoltage, jlabelRate;
    DefaultTableModel defTableModelScan, defTableModelInventoryRfid, defTableModelInventoryBarcode;
    JTable jtableScan;
    JTabbedPane jtabbedPane; JButton jbuttonTabbedPaneControl;
    JTextArea jtextArea;

    CS108JavaLibrary cs108javalibrary;

    final int itableRowHeight = 50;
    final int textfieldEpcWidth = 250;
    final int textfieldHeight = 35;
    final int textfieldWidth = 75;

    final int sleepInRunnableVoltageUpdate = 5000;  //expected data every 4000
    final int sleepInRunnableScan = 1000;
    final int sleepInInventory = 250;

    final String versionDemoApp = "1.0.2";
    final String versionDisconnect = " Disconnected.";

    final String scanStartText = "Scan";
    final String connectStartText = "Connect";
    final String inventoryRfidStartText = "Inventory Rfid";
    final String inventoryBarcodeStartText = "Inventory Barcode";
    final String searchRfidStartText = "Geiger Search";
    final String readWriteStartText = "Read/Write";
    final String lockillStartText = "Lock/Kill";

    final String scanStopText = "Stop scanning";
    final String connectStopText = "Disconnect";
    final String inventoryStopText = "Stop inventory";
    final String searchStopText = "Stop searching";
    final String readwriteStopText = "Stop Read/Write";
    final String lockillStopText = "Stop Lock/Kill";

    final String testStartText = "Init test";
    final String testStopText = "Stop testing";
    final String testaStartText = "Test A";
    final String testaStopText = "Stop testing A";
    final String testbStartText = "Test B";
    final String testbStopText = "Stop testing B";

    public static void main(String[] args) {
        new CS108JavaDemoApp();
    }

    CS108JavaDemoApp() {
        cs108javalibrary = new CS108JavaLibrary(jtextArea);

        JFrame jframeMain = new JFrame("CS108JavaDemoApp v" + versionDemoApp + "  Cs108JavaLibrary v" + cs108javalibrary.getlibraryVersion());
        jframeMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframeMain.setMinimumSize(new Dimension(400,300));
        jframeMain.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                System.out.println("CLOSE is pressed");
                cs108javalibrary.disconnect(false);
                super.windowClosing(windowEvent);
            }
        });

        JPanel jpanelControls = getPanelControls(jframeMain, "Main");

        jbuttonScan = new JButton(scanStartText);
        jbuttonScan.setPreferredSize(new Dimension(200, 200));
        jbuttonScan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("SCAN/STOP is pressed");
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        scanHandler();
                    }
                });
                t.start();
            }
        });

        jbuttonConnect = new JButton(connectStartText);
        jbuttonConnect.setVisible(false);
        jbuttonConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("CONNECT/DISCONNECT is pressed");
                if (connecting) return;
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        connectHandler();
                    }
                });
                t.start();
            }
        });

        jbuttonInventoryRfid = new JButton(inventoryRfidStartText);
        jbuttonInventoryRfid.setVisible(false);
        jbuttonInventoryRfid.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inventoryRfidHandler(false);
            }
        });

        jbuttonInventoryBarcode = new JButton(inventoryBarcodeStartText);
        jbuttonInventoryBarcode.setVisible(false);
        jbuttonInventoryBarcode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inventoryBarcodeHandler();
            }
        });

        jbuttonSearchRfid = new JButton(searchRfidStartText);
        jbuttonSearchRfid.setVisible(false);
        jbuttonSearchRfid.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inventoryRfidHandler(true);
            }
        });

        jbuttonReadWrite = new JButton(readWriteStartText);
        jbuttonReadWrite.setVisible(false);
        jbuttonReadWrite.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                readwriteHandler();
            }
        });

        jbuttonLockill = new JButton(lockillStartText);
        jbuttonLockill.setVisible(false);
        jbuttonLockill.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lockillHandler();
            }
        });


        JPanel jpanelButtons = new JPanel(new GridLayout(2, 4));
        jpanelButtons.add(jbuttonScan);
        jpanelButtons.add(jbuttonConnect);
        jpanelButtons.add(jbuttonInventoryRfid);
        jpanelButtons.add(jbuttonInventoryBarcode);
        jpanelButtons.add(jbuttonSearchRfid);
        jpanelButtons.add(jbuttonReadWrite);
        jpanelButtons.add(jbuttonLockill);

        jlabelVersions = new JLabel(versionDisconnect);
        jlabelVoltage = new JLabel();
        jlabelRate = new JLabel();

        JPanel jpanelLabels = new JPanel(new FlowLayout());
        jpanelLabels.add(jlabelVersions);
        jpanelLabels.add(jlabelVoltage);
        jpanelLabels.add(jlabelRate);
        jpanelLabels.setMaximumSize(new Dimension(100,30));

        defTableModelScan = new DefaultTableModel(null, new String[] {
            "Index", "Name", "Address", "RSSI", "Connected"
        });

        jtableScan = new JTable(defTableModelScan) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                int rendererWidth = component.getPreferredSize().width;
                TableColumn tableColumn = getColumnModel().getColumn(column);
                tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
                return component;
             }
        };
        jtableScan.setRowHeight(itableRowHeight);
        jtableScan.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);   //AUTO_RESIZE_OFF, AUTO_RESIZE_ALL_COLUMNS
        jtableScan.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jtableScan.setDragEnabled(false);

        JScrollPane jscrollPaneScan = new JScrollPane(jtableScan);
        jscrollPaneScan.setMinimumSize(new Dimension(200, 100));
        jscrollPaneScan.revalidate ();

        defTableModelInventoryRfid = new DefaultTableModel(null, new String[] {
            "Index", "PC", "EPC", "RSSI", "Count" });

        JTable jtableInventoryRfid = new JTable(defTableModelInventoryRfid);
        jtableInventoryRfid.setRowHeight(itableRowHeight);
        jtableInventoryRfid.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);   //AUTO_RESIZE_OFF, AUTO_RESIZE_ALL_COLUMNS
        jtableInventoryRfid.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jtableInventoryRfid.setDragEnabled(false);

        JScrollPane jscrollPaneInventoryRfid = new JScrollPane(jtableInventoryRfid);
        jscrollPaneInventoryRfid.setMinimumSize(new Dimension(200, 100));
        jscrollPaneInventoryRfid.revalidate ();

        defTableModelInventoryBarcode = new DefaultTableModel(null, new String[] {
            "Index", "Barcode", "Count" });

        JTable jtableInventoryBarcode = new JTable(defTableModelInventoryBarcode) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                int rendererWidth = component.getPreferredSize().width;
                TableColumn tableColumn = getColumnModel().getColumn(column);
                tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
                return component;
             }
        };
        jtableInventoryBarcode.setRowHeight(itableRowHeight);
        jtableInventoryBarcode.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);   //AUTO_RESIZE_OFF, AUTO_RESIZE_ALL_COLUMNS
        jtableInventoryBarcode.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jtableInventoryBarcode.setDragEnabled(false);

        JScrollPane jscrollPaneInventoryBarcode = new JScrollPane(jtableInventoryBarcode);
        jscrollPaneInventoryBarcode.setMinimumSize(new Dimension(200, 100));
        jscrollPaneInventoryBarcode.revalidate ();

        jtabbedPane = new JTabbedPane();
        UIManager.getLookAndFeelDefaults().put("TabbedPane:TabbedPaneTab.contentMargins", new Insets(200, 200, 200, 200));
        jtabbedPane.addTab("Scan", jscrollPaneScan);                //0
        jtabbedPane.addTab("RFID", jscrollPaneInventoryRfid);       //1
        jtabbedPane.addTab("Barcode", jscrollPaneInventoryBarcode); //2
        jtabbedPane.addTab("Setting", getPanelSetting());           //3
        jtabbedPane.addTab("Filters", getPanelFilter());            //4
        jtabbedPane.addTab("Search", getPanelSearch());         //5
        jtabbedPane.addTab("Read/Write", getPanelReadWrite());  //6
        jtabbedPane.addTab("Lock/Kill", getPanelLock());        //7

        Dimension dimTab = new Dimension(60,50);
        for (int i = 0; i < jtabbedPane.getTabCount(); i++) {
            String tabName = jtabbedPane.getTitleAt(i);
            JLabel labelTab = new JLabel(tabName);
            if (labelTab.getText().matches("Read/Write")) {
                labelTab.setPreferredSize(new Dimension(80,50));
            } else labelTab.setPreferredSize(dimTab);
            jtabbedPane.setTabComponentAt(i, labelTab);
        }

        jtabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (e.getSource() instanceof JTabbedPane) {
                    JTabbedPane pane = (JTabbedPane) e.getSource();
                    int selectIndex = pane.getSelectedIndex();
                    switch (selectIndex) {
                        case 0:
                        case 1:
                        case 2:
                            jbuttonTabbedPaneControl.setText("Clear"); jbuttonTabbedPaneControl.setVisible(true);
                            break;
                        default:
                            if (cs108javalibrary.isBleConnected()) {
                                if (selectIndex == 3)    showAllSetting();
                                if (selectIndex == 4)    showAllFilter();
                                showOnBoard();
                            }
                            if (selectIndex == 3 || selectIndex == 4) {
                                jbuttonTabbedPaneControl.setText("Save"); jbuttonTabbedPaneControl.setVisible(true);
                            } else {
                                jbuttonTabbedPaneControl.setVisible(false);
                            }
                            break;
                    }
                }
             }
        });

        Dimension dimButton = new Dimension(50, 50);
        jbuttonTabbedPaneControl = new JButton("Clear");
//        jbuttonTabbedPaneControl.setPreferredSize(dimButton);
        jbuttonTabbedPaneControl.setMinimumSize(dimButton);
        jbuttonTabbedPaneControl.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch(jtabbedPane.getSelectedIndex()) {
                    case 0:
                        defTableModelScan.setRowCount(0);
                        break;
                    case 1:
                        defTableModelInventoryRfid.setRowCount(0);
                        break;
                    case 2:
                        defTableModelInventoryBarcode.setRowCount(0);
                        break;
                    case 3:
                        if (cs108javalibrary.isBleConnected()) {
                            try {
                                int antennaCycle = Integer.valueOf(jtextfieldAntennaCycle4Setting.getText());
                                long antennaDwell = Integer.valueOf(jtextfieldAntennaDwell4Setting.getText());
                                long antennaPower = Integer.valueOf(jtextfieldAntennaPower4Setting.getText());
                                int queryTarget = jcomboboxQueryTarget4Setting.getSelectedIndex();
                                int querySession = jcomboxQuerySession4Setting.getSelectedIndex();
                                int querySelect = jcomboxQuerySelect4Setting.getSelectedIndex();
                                boolean queryAlgo = jcheckboxQueryAlgorithm4Setting.isSelected();
                                int queryProfile = Integer.valueOf(jtextfieldQueryProfile4Setting.getText());
                                int dynamicStartQ = Integer.valueOf(jtextfieldDynamicStartQ4Setting.getText());
                                int dynamicMaxQ = Integer.valueOf(jtextfieldDynamicMaxQ4Setting.getText());
                                int dynamicMinQ = Integer.valueOf(jtextfieldDynamicMinQ4Setting.getText());
                                int dynamicRetry = Integer.valueOf(jtextfieldDynamicRetry4Setting.getText());
                                int fixedValueQ = Integer.valueOf(jtextfieldFixedQ4Setting.getText());
                                int fixedRetry = Integer.valueOf(jtextfieldFixedRetry4Setting.getText());
                                boolean fixedRunTilZero = jcheckboxRunTilZero4Setting.isSelected();

                                cs108javalibrary.setAntennaCycle(antennaCycle);
                                cs108javalibrary.setAntennaDwell(antennaDwell);
                                cs108javalibrary.setPowerLevel(antennaPower);
                                cs108javalibrary.setTagGroup(querySelect, querySession, queryTarget);
                                cs108javalibrary.setInvAlgo(queryAlgo);
                                cs108javalibrary.setCurrentLinkProfile(queryProfile);
                                cs108javalibrary.setDynamicQParms(dynamicStartQ, dynamicMinQ, dynamicMaxQ, dynamicRetry);
                                cs108javalibrary.setFixedQParms(fixedValueQ, fixedRetry, fixedRunTilZero);
                                cs108javalibrary.saveSetting2File();

                                showAllSetting();
                            } catch (Exception ex) { }
                        }
                        break;
                    case 4:
                        if (cs108javalibrary.isBleConnected()) {
                            try {
                                String preMaskData = jtextfieldMaskData4PreFilter.getText();
                                int preOffset = Integer.valueOf(jtextfieldOffset4PreFilter.getText()) + 32;
                                int preMemoryBank = jcomboboxMemoryBank4PreFilter.getSelectedIndex();
                                int preAction = jcomboboxActio4PreFilter.getSelectedIndex();
                                int preTarget = jcomboboxTarget4PreFilter.getSelectedIndex();
                                boolean preEnable = jcheckboxEnable4PreFilter.isSelected();
                                int preIndex = Integer.valueOf(jtextfieldIndex4PreFilter.getText());
                                String postMaskData = jtextfieldMaskData4PostFilter.getText();
                                int postOffset = Integer.valueOf(jtextfieldOffset4PostFilter.getText());
                                boolean postMatchEpc = jcheckboxMatchEPC4PosFilter.isSelected();
                                boolean postEnable = jcheckboxEnable4PostFilter.isSelected();

                                cs108javalibrary.setInvSelectIndex(preIndex);
                                cs108javalibrary.setSelectCriteria(preEnable, preTarget, preAction, preMemoryBank, preOffset, preMaskData);
                                cs108javalibrary.setPostMatchCriteria(postEnable, postMatchEpc, postOffset, postMaskData);

                                showAllFilter();
                            } catch (Exception ex) { }
                        }
                        break;
                }
                if (isConnected == false) {
                    jbuttonConnect.setVisible(false);
                }
            }
        });

        jtextArea = new JTextArea();
        jtextArea.setRows(10);

        DefaultCaret caret = (DefaultCaret)jtextArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JScrollPane jscrollPaneTextArea = new JScrollPane(jtextArea);
        jscrollPaneTextArea.setMinimumSize(new Dimension(200, 100));

        JButton jbuttonClearTextArea = new JButton("Clear");
//        jbuttonClearTextArea.setPreferredSize(dimButton);
        jbuttonClearTextArea.setMinimumSize(dimButton);
        jbuttonClearTextArea.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jtextArea.setText("");
            }
        });

        GroupLayout layout = new GroupLayout(jframeMain.getContentPane());
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(jpanelButtons)
                        .addComponent(jpanelControls))
                .addComponent(jpanelLabels)
                .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                .addComponent(jtabbedPane)
                                .addComponent(jbuttonTabbedPaneControl))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                .addComponent(jscrollPaneTextArea)
                                .addComponent(jbuttonClearTextArea)))
        );
        layout.setHorizontalGroup(layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                        .addComponent(jpanelButtons)
                        .addComponent(jpanelControls))
                .addComponent(jpanelLabels)
                .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                .addComponent(jtabbedPane)
                                .addComponent(jscrollPaneTextArea))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                .addComponent(jbuttonTabbedPaneControl)
                                .addComponent(jbuttonClearTextArea)))
        );
        jframeMain.setLayout(layout);

        jframeMain.pack();
        jframeMain.setVisible(true);

        Thread threadVoltage = new Thread(runnableVoltageUpdate);
        threadVoltage.start();

        System.out.println("Cs108JavaDemoApp() is OK");
    }

    Runnable runnableVoltageUpdate = new Runnable() {
        @Override
        public void run() {
            int batteryCount = 0;
            while(true) {
                try {
                    int batteryCountNew = cs108javalibrary.getBatteryCount();
                    if (batteryCount != batteryCountNew) {
                        batteryCount = batteryCountNew;
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Calendar cal = Calendar.getInstance();
                        jlabelVoltage.setText("Battery: "+ String.format("%.3f", ((float)cs108javalibrary.getBatteryLevel())/1000) + "V  Time: "+ dateFormat.format(cal.getTime()) + "  ");
                    }
                    Thread.sleep(sleepInRunnableVoltageUpdate);
                } catch (InterruptedException ex) {
                    //
                }
            }
        }
    };

    JPanel getPanelControls(JFrame jframe, String sCloseText) {
        JButton jbuttonExit = new JButton("EXIT");
        jbuttonExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(sCloseText + ": EXIT is pressed");
                if (sCloseText.contains("Main")) {
                    cs108javalibrary.disconnect(false);
                    System.exit(0);
                } else {
                    closeListFrame();
                    jframe.dispose();
                }
            }
        });

        JButton jbuttonMin = new JButton("Minimize");
        jbuttonMin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(sCloseText + ": HIDE is pressed");
                jframe.setState(Frame.ICONIFIED);
            }
        });

        JButton jbuttonMax = new JButton("Maximize");
        jbuttonMax.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(sCloseText + ": MAXIMUM/NORMAL is pressed");
                if (jframe.getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                    jframe.setExtendedState(JFrame.NORMAL);
                    jbuttonMax.setText("Maximize");
                } else {
                    jframe.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    jbuttonMax.setText("Normalize");
                }
            }
        });

        JPanel jpanelControls = new JPanel();
        jpanelControls.setLayout(new GridLayout(3, 1));
        jpanelControls.add(jbuttonExit);
        jpanelControls.add(jbuttonMin);
        jpanelControls.add(jbuttonMax);
        return jpanelControls;
    }

    void showOnBoard() {
        try {
            Runtime.getRuntime().exec("onboard");
        } catch (IOException ex) {
            System.out.println("Exception = " + ex.toString());
        }
    }

    boolean scanning = false;
    void scanHandler() {
        if (scanning == false && scanRunning == false) {
            scanning = true; defTableModelScan.setRowCount(0); 
            cs108javalibrary.scanLeDevice(true);
            Thread thread1 = new Thread(runnableScan);
            thread1.start();
            jbuttonScan.setText(scanStopText); jbuttonConnect.setVisible(false);
        } else {
            scanning = false;
            cs108javalibrary.scanLeDevice(false);
        }
    }

    boolean scanRunning = false;
    Runnable runnableScan = new Runnable() {
        @Override
        public void run() {
            scanRunning = true;
            int listIndex = 0;
            System.out.println("runnableScan(): enter the loop");
            while(scanning) {
                try {
                    List<CS108JavaLibrary.ScanResult> listScanResult = cs108javalibrary.getScanning();
                    for (CS108JavaLibrary.ScanResult scanResult: listScanResult) {
                        boolean found = false;
                        if (defTableModelScan.getRowCount() != 0) {
                            for (int k = 0; k < defTableModelScan.getRowCount(); k++) {
                                String elementName = defTableModelScan.getValueAt(k, 2).toString();
                                if (elementName.contains(scanResult.address)) {
                                    defTableModelScan.setValueAt(scanResult.rssi, k, 3);
                                    defTableModelScan.setValueAt(scanResult.connected, k, 4);
                                    found = true;
                                    break;
                                }
                            }
                        }
//                        System.out.println("runnableScan(): found = " + found + ", rssi = " + scanResult.rssi);
                        if (found == false && scanResult.rssi != 0) {
                            Object[] data = new Object[5];
                            data[0] = ++listIndex;
                            data[1] = scanResult.name + "  ";
                            data[2] = scanResult.address;
                            data[3] = scanResult.rssi;
                            data[4] = scanResult.connected;
                            defTableModelScan.addRow(data);
                        }
                        jtabbedPane.setSelectedIndex(0);
                    }
                    Thread.sleep(sleepInRunnableScan);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            scanRunning = false;
            System.out.println("runnableScan(): exit the loop");
            jbuttonScan.setText(scanStartText); if (listIndex != 0) jbuttonConnect.setVisible(true);
        }
    };

    boolean isConnected = false;
    boolean connecting = false;
    void connectHandler() {
        connecting = true;
        if (isConnected == false) {
            String connectingAddress = null;
            int selectedRow = jtableScan.getSelectedRow();
            if (selectedRow >= 0) {
                try {
                    Object selectedObject = defTableModelScan.getValueAt(jtableScan.getSelectedRow(), 2);
                    System.out.println("connectHandler(): the selected string = " + selectedObject.toString());
                    connectingAddress = selectedObject.toString();
                } catch (Exception ex) { }
            }
            if (connectingAddress == null) {
                System.out.println("\nNo selected Device for connection. Cannot connect"); return;
            }
            jbuttonConnect.setText("Connecting"); jbuttonConnect.repaint();
            if (cs108javalibrary.connect(connectingAddress)) {
                isConnected = true;
                cs108javalibrary.setSelectCriteria(false, 4, 0, 1, 32, "");

                jlabelVersions.setText("Connected  Firmware: Controller: " + cs108javalibrary.hostProcessorICGetFirmwareVersion()
                        + "; BT: " + cs108javalibrary.bluetoothICGetFirmwareVersion()
                        + "; RFID: " + cs108javalibrary.getMacVer()
                        + "  ");
                System.out.println("Connection Success");
                jbuttonScan.setVisible(false);
                jbuttonConnect.setText(connectStopText);
                setVisibleConnectStandby();

                jtextfield4Search.setText(String.valueOf(cs108javalibrary.getPwrlevel()));
                //showAllSetting();
            } else {
                jtextArea.append("\nConnection failure");
                jbuttonConnect.setText(connectStartText);
            }
            System.out.println("connectHandler(): isBleConnected() = " + cs108javalibrary.isBleConnected());
        } else {
            jbuttonConnect.setText("Disconnecting");
            if (cs108javalibrary.disconnect(false)) {
                isConnected = false;
                jlabelVersions.setText(versionDisconnect);
                jtextArea.append("\nDisconnection Success");
                jbuttonScan.setVisible(true);
                jbuttonConnect.setText(connectStartText);

                jbuttonInventoryRfid.setVisible(false);
                jbuttonInventoryBarcode.setVisible(false);
                jbuttonSearchRfid.setVisible(false);
                jbuttonReadWrite.setVisible(false);
                jbuttonLockill.setVisible(false);

                jtextfield4Search.setText("");

                jtextfieldAntennaCycle4Setting.setText("");
                jtextfieldAntennaDwell4Setting.setText("");
                jtextfieldAntennaPower4Setting.setText("");

                jcomboboxQueryTarget4Setting.setSelectedIndex(0);
                jcomboxQuerySession4Setting.setSelectedIndex(0);
                jcomboxQuerySelect4Setting.setSelectedIndex(0);
                jcheckboxQueryAlgorithm4Setting.setSelected(false);
                jtextfieldQueryProfile4Setting.setText("");

                jtextfieldDynamicStartQ4Setting.setText("");
                jtextfieldDynamicMaxQ4Setting.setText("");
                jtextfieldDynamicMinQ4Setting.setText("");
                jtextfieldDynamicRetry4Setting.setText("");

                jtextfieldFixedQ4Setting.setText("");
                jtextfieldFixedRetry4Setting.setText("");
                jcheckboxRunTilZero4Setting.setSelected(false);

                jtextfieldIndex4PreFilter.setText("");
                jcheckboxEnable4PreFilter.setSelected(false);
                jcomboboxTarget4PreFilter.setSelectedIndex(0);
                jcomboboxActio4PreFilter.setSelectedIndex(0);
                jcomboboxMemoryBank4PreFilter.setSelectedIndex(0);
                jtextfieldOffset4PreFilter.setText("");
                jtextfieldMaskData4PreFilter.setText("");

                jcheckboxEnable4PostFilter.setSelected(false);
                jcheckboxMatchEPC4PosFilter.setSelected(false);
                jtextfieldOffset4PostFilter.setText("");
                jtextfieldMaskData4PostFilter.setText("");
            } else {
                jtextArea.append("\nDisconnection failure");
                jbuttonConnect.setText(connectStopText);
            }
        }
        connecting = false;
    }

    void setVisibleConnectStandby() {
        jbuttonInventoryRfid.setVisible(true);
        jbuttonInventoryBarcode.setVisible(true);
        jbuttonSearchRfid.setVisible(true);
        jbuttonReadWrite.setVisible(true);
        jbuttonLockill.setVisible(true);
        for (int i = 0; i < jtabbedPane.getTabCount(); i++)
            jtabbedPane.setEnabledAt(i, true);
    }

    void setVisibleConnectOperating() {
        jbuttonInventoryRfid.setVisible(false);
        jbuttonInventoryBarcode.setVisible(false);
        jbuttonSearchRfid.setVisible(false);
        jbuttonReadWrite.setVisible(false);
        jbuttonLockill.setVisible(false);
        for (int i = 0; i < jtabbedPane.getTabCount(); i++)
            jtabbedPane.setEnabledAt(i, false);
    }

    boolean inventoringRfid = false;
    boolean searchingRfid = false;
    void inventoryRfidHandler(boolean searchRequest) {
        if (isConnected == false) {
            jtextArea.append("\nNo connection. Cannot inventory"); return;
        }
        if (inventoringRfid == false && inventoryRfidRunning == false) {
            this.searchingRfid = searchRequest;
            if (searchRequest) {
                String searchingAddress = null; int powerLevel = 300;
                try {
                    searchingAddress = jtextfieldAddres4Search.getText();
                    System.out.println("inventoryRfidHandler(): the selected string = " + searchingAddress);
                    int powerLevelA = Integer.parseInt(jtextfield4Search.getText());
                    if (powerLevelA >= 0 && powerLevelA <= 300) {
                        powerLevel = powerLevelA;
                    }
                } catch (Exception ex) { }
                if (cs108javalibrary.setSelectedTag(searchingAddress, powerLevel, false) == false) return;
            }
            inventoringRfid = true;
            if (searchingRfid == false) {
                defTableModelInventoryRfid.setRowCount(0);
            }
            cs108javalibrary.getMacLastCommandDuration(true);
            cs108javalibrary.startOperation(CS108JavaLibrary.OperationTypes.TAG_INVENTORY);
            Thread thread1 = new Thread(runnableInventory);
            thread1.start();
 //           SwingUtilities.invokeLater(runnableInventory);
            setVisibleConnectOperating();
            if (searchRequest)  { jtabbedPane.setSelectedIndex(5); jbuttonSearchRfid.setText(searchStopText); jbuttonSearchRfid.setVisible(true); }
            else                { jtabbedPane.setSelectedIndex(1); jbuttonInventoryRfid.setText(inventoryStopText); jbuttonInventoryRfid.setVisible(true); }
        } else if ((searchRequest && searchingRfid) || (searchRequest == false && searchingRfid == false)) {
            inventoringRfid = false;
            cs108javalibrary.abortOperation();
        }
    }

    boolean inventoryRfidRunning = false;
    Runnable runnableInventory = new Runnable() {
        @Override
        public void run() {
            inventoryRfidRunning = true;

            int total = 0; int listIndex = 0;
            double rssi;
            long firstTime = 0;
            long lastTime = 0;

            long timeMillis = System.currentTimeMillis();
            long timeMillisUpdate = System.currentTimeMillis();
            boolean ending = false;
            boolean timeout = false;
            CS108JavaLibrary.Rx000pkgData rx000pkgData;

            System.out.println("runnableInventory(): enter the loop");
            cs108javalibrary.appendToLog("runnableInventory(): enter the loop, lastCommandDuration = " + cs108javalibrary.getMacLastCommandDuration(false));
            while (cs108javalibrary.isBleConnected() && ending == false && timeout == false && isConnected && inventoringRfid) {
                rx000pkgData = cs108javalibrary.onRFIDEvent();
                if (cs108javalibrary.mrfidToWriteSize() != 0) {
                    System.out.println("runnableInventory(): non-zero mrfidToWriteSize !!!");
                    try {
                        Thread.sleep(sleepInInventory);
                    } catch (InterruptedException ex) { }
                    timeMillis = System.currentTimeMillis();
                } else if (rx000pkgData == null) {
                //    System.out.println("runnableInventory(): NULL rx000pkgData !!!");
                } else {
                    //System.out.println("runnableInventory(): non-NULL rx000pkgData");
                    if (rx000pkgData.responseType == null) {
                        System.out.println("runnableInventory(): null response");
                    } else if (rx000pkgData.responseType == CS108JavaLibrary.HostCmdResponseTypes.TYPE_18K6C_INVENTORY) {
                        int epcLength = (rx000pkgData.dataValues[12] >> 3) * 2;
                        if (rx000pkgData.dataValues.length < 12 + 4) {
                            System.out.println("runnableInventory(): invalid rx000pkgData.dataBytes = " + cs108javalibrary.byteArrayToString(rx000pkgData.dataValues));
                        } else {
                            total++;
                            long time1 = rx000pkgData.dataValues[3] & 0x00FF;
                            time1 = time1 << 8;
                            time1 |= rx000pkgData.dataValues[2] & 0x00FF;
                            time1 = time1 = time1 << 8;
                            time1 |= rx000pkgData.dataValues[1] & 0x00FF;
                            time1 = time1 = time1 << 8;
                            time1 |= rx000pkgData.dataValues[0] & 0x00FF;
                            if (firstTime == 0 || time1 <= firstTime) {
                                firstTime = time1;
                                lastTime = time1;
                            } else if (time1 > lastTime) {
                                lastTime = time1;
                            }

                            byte nbRssi = rx000pkgData.dataValues[5];
                            byte mantissa = nbRssi;
                            mantissa &= 0x07;
                            byte exponent = nbRssi;
                            exponent >>= 3;
                            rssi = 20 * Math.log10(Math.pow(2, exponent) * (1 + (mantissa / Math.pow(2, 3))));

                            byte[] epcData = new byte[rx000pkgData.dataValues.length - 12]; //[epcLength];
                            System.arraycopy(rx000pkgData.dataValues, 12, epcData, 0, epcData.length);
                            String strEpcData = cs108javalibrary.byteArrayToString(epcData);
                            String sRssi = String.format("%.1f", rssi);
                            System.out.println("Hello: strEpcData = " + strEpcData);

                            boolean match = false;
                            String strEpc = strEpcData.substring(4, strEpcData.length() - 4);
                            String strPc = strEpcData.substring(0, 4);
                            String strCrc16 = strEpcData.substring(strEpcData.length() - 4);
                            timeMillis = System.currentTimeMillis();
 
                            if (searchingRfid == false) {
                                if (defTableModelInventoryRfid.getRowCount() != 0) {
                                    for (int k = 0; k < defTableModelInventoryRfid.getRowCount(); k++) {
                                        String elementName = defTableModelInventoryRfid.getValueAt(k, 2).toString();
                                        if (elementName.contains(strEpc)) {
                                            int count = Integer.parseInt(defTableModelInventoryRfid.getValueAt(k, 4).toString(), 10);
                                            defTableModelInventoryRfid.setValueAt(count+1, k, 4);
                                            match = true;
                                            break;
                                        }
                                    }
                                }
                                if (match == false) {
                                    Date date = new Date();
                                    Object[] data = new Object[5];
                                    data[0] = ++listIndex;
                                    data[1] = strPc + "  ";
                                    data[2] = strEpc;
                                    data[3] = sRssi;
                                    data[4] = 1;
                                    defTableModelInventoryRfid.addRow(data);
                                }
                            } else {
                                jprogressBar4Search.setValue((int)rssi);
                                jprogressBar4Search.setString(sRssi +"dBm");
                                jlabelAddres4Search.setText("Tag = " + strEpc);
                                if (jcheckbox4Search.isSelected()) {
                                    if ((int)rssi > ithresholdRssi4Search) {
                                        java.awt.Toolkit.getDefaultToolkit().beep();
                                    }
                                }
                            }
                            if (lastTime > firstTime && total > 1)
                                jlabelRate.setText("Rate(" + total + "/" + (lastTime - firstTime) + "=" + (((long)total)*1000)/(lastTime - firstTime) + " tags/sec).");
                        }
                    } else if (rx000pkgData.responseType == CS108JavaLibrary.HostCmdResponseTypes.TYPE_ANTENNA_CYCLE_END) {
                        System.out.println("runnableInventory(): rx000pkgData.TYPE_ANTENNA_CYCLE_END is received");
                        timeMillis = System.currentTimeMillis();
                    } else if (rx000pkgData.responseType == CS108JavaLibrary.HostCmdResponseTypes.TYPE_COMMAND_END) {
                        System.out.println("runnableInventory(): rx000pkgData.TYPE_COMMAND_END is received");
                        ending = true;
                    }
                    else    System.out.println("runnableInventory(): valid rx000pkgData is received");
                }
                if (timeMillisUpdate > System.currentTimeMillis() + 1000) {
                    timeMillisUpdate = System.currentTimeMillis();
                    System.out.println("runnableInventory(): looping");
                }
                if (System.currentTimeMillis() - timeMillis > 10000) {
                    System.out.println("runnableInventory(): Exit as TIMEOUT");
                    timeout = true;
                }
            }
            System.out.println("runnableInventory(): exit the loop");
            if (cs108javalibrary.isBleConnected() && isConnected) {
                System.out.println("runnableInventory(): Ending Inventory");
                if (false) {
                    cs108javalibrary.disconnect(false);
                    try { Thread.sleep(5000); } catch (Exception ex) { }
                    cs108javalibrary.connect();
                } else {
                    cs108javalibrary.getMacLastCommandDuration(true);
                    cs108javalibrary.abortOperation();
                    timeMillis = System.currentTimeMillis();
                    while (cs108javalibrary.isBleConnected()) {
                        rx000pkgData = cs108javalibrary.onRFIDEvent();                    
                        if (cs108javalibrary.mrfidToWriteSize() == 0 && rx000pkgData == null) {
                            if (System.currentTimeMillis() - timeMillis > 2000) {
                                System.out.println("runnableInventory(): Exit as TIMEOUT");
                                break;
                            }
                        } else timeMillis = System.currentTimeMillis();
                    }
                    if (timeout) {
                        long lastCommandDuration = cs108javalibrary.getMacLastCommandDuration(false);
                        cs108javalibrary.appendToLog("runnableInventory(): lastCommandDuration = " + lastCommandDuration);
                        if (lastCommandDuration == 0)
                            System.out.println("runnableInventory(): Exit as RFID RESET");
                        else
                            System.out.println("runnableInventory(): Confirmed Exit as TIMEOUT");
                    }
                }
            }
            System.out.println("runnableInventory(): Ending finishes, isBleConnected = " + cs108javalibrary.isBleConnected());
            if (cs108javalibrary.isBleConnected() == false) {
                 try { Thread.sleep(5000); } catch (Exception ex) { }
                 cs108javalibrary.connect();
                 System.out.println("runnableInventory(): Ending finishes with RE-CONNECT, isBleConnected = " + cs108javalibrary.isBleConnected());
            }

            inventoryRfidRunning = false;
            jbuttonInventoryRfid.setText(inventoryRfidStartText);
            jbuttonSearchRfid.setText(searchRfidStartText);
            setVisibleConnectStandby();
            if (cs108javalibrary.isBleConnected() == false)  connectHandler();
            cs108javalibrary.inventoryOperating = false;
        }
    };

    boolean inventoringBarcode = false;
    void inventoryBarcodeHandler() {
        System.out.println("inventoryBarcodeHandler(): Enter");
        if (isConnected == false) {
            jtextArea.append("\nNo connection. Cannot inventory"); return;
        }
        if (inventoringBarcode == false && inventoryBarcodeRunning == false) {
            inventoringBarcode = true; defTableModelInventoryBarcode.setRowCount(0);
            cs108javalibrary.setBarcodeOn(true);
            Thread thread1 = new Thread(runnableInventoryBarcode);
            thread1.start();
            setVisibleConnectOperating();
            jtabbedPane.setSelectedIndex(2); jbuttonInventoryBarcode.setText(inventoryStopText); jbuttonInventoryBarcode.setVisible(true);
        } else {
            inventoringBarcode = false;
            cs108javalibrary.setBarcodeOn(false);
//            cs108javalibrary.abortOperation();
        }
        System.out.println("inventoryBarcodeHandler(): Exit");
    }

    boolean inventoryBarcodeRunning = false;
    Runnable runnableInventoryBarcode = new Runnable() {
        @Override
        public void run() {
            inventoryBarcodeRunning = true;

            int total = 0;
            int listIndex = 0;

            System.out.println("runnableInventoryBarcode(): Enter loop");
            while (cs108javalibrary.isBleConnected() && isConnected && inventoringBarcode) {
                try { 
                    Thread.sleep(sleepInInventory);
                } catch (InterruptedException ex) { }
                byte[] onBarcodeEvent = cs108javalibrary.onBarcodeEvent();
                if (onBarcodeEvent != null) {
                    String output = new String(onBarcodeEvent);
                    if (output != null) {
                        total++;
                        boolean match = false;
                        if (defTableModelInventoryBarcode.getRowCount() != 0) {
                            for (int k = 0; k < defTableModelInventoryBarcode.getRowCount(); k++) {
                                String elementName = defTableModelInventoryBarcode.getValueAt(k, 1).toString();
                                if (elementName.contains(output)) {
                                    int count = Integer.parseInt(defTableModelInventoryBarcode.getValueAt(k, 2).toString(), 10);
                                    defTableModelInventoryBarcode.setValueAt(count+1, k, 2);
                                    match = true;
                                    break;
                                }
                            }
                        }
                        if (match == false) {
                            Date date = new Date();
                            Object[] data = new Object[5];
                            data[0] = ++listIndex;
                            data[1] = output;
                            data[2] = 1;
                            defTableModelInventoryBarcode.addRow(data);
                        }
                        jlabelRate.setText("Total(" + total + " tags).");
                    }
                }
            }
            System.out.println("runnableInventoryBarcode(): Exit loop");

            inventoryBarcodeRunning = false;
            jbuttonInventoryBarcode.setText(inventoryBarcodeStartText); setVisibleConnectStandby();
        }
    };

    JTextField jtextfieldAddres4Search;
    JProgressBar jprogressBar4Search; JLabel jlabelAddres4Search; JCheckBox jcheckbox4Search; JTextField jtextfield4Search;
    JLabel jlabelThreshold4Search; int ithresholdRssi4Search = 50;
    JPanel getPanelSearch() {
        jprogressBar4Search = new JProgressBar(0, 100);
        jprogressBar4Search.setValue(50);
        jprogressBar4Search.setStringPainted(true);

        JButton jbuttonAddres4Search  = new JButton("Select Mask data");
        jbuttonAddres4Search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showListFrame(jtextfieldAddres4Search);
            }
        });
        jtextfieldAddres4Search = new JTextField();
        jtextfieldAddres4Search.setMaximumSize(new Dimension(textfieldEpcWidth,textfieldHeight));

        jlabelAddres4Search = new JLabel("Initial address");
       
        JSlider jslider4Search = new JSlider(JSlider.HORIZONTAL, 0, 100, ithresholdRssi4Search);
        jslider4Search.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent event) {
                ithresholdRssi4Search = jslider4Search.getValue();
                jlabelThreshold4Search.setText("Threshold = " + ithresholdRssi4Search + "dBm");
            }
        });

        jcheckbox4Search = new JCheckBox("Tone");
        JLabel jlabelAntennaPower4Search = new JLabel("Antenna power(0-30dBm)");
        jtextfield4Search = new JTextField();
        jtextfield4Search.setMaximumSize(new Dimension(textfieldWidth,textfieldHeight));

        jlabelThreshold4Search = new JLabel("Threshold = " + ithresholdRssi4Search + "dBm");

        JPanel jpanelSearch = new JPanel();

        GroupLayout layoutSearch = new GroupLayout(jpanelSearch);
        layoutSearch.setVerticalGroup(layoutSearch.createSequentialGroup()
                .addGroup(layoutSearch.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(jbuttonAddres4Search)
                        .addComponent(jtextfieldAddres4Search))
                .addComponent(jprogressBar4Search)
                .addComponent(jlabelAddres4Search)
                .addComponent(jslider4Search)
                .addGroup(layoutSearch.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(jcheckbox4Search)
                        .addComponent(jlabelThreshold4Search))
                .addGroup(layoutSearch.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(jlabelAntennaPower4Search)
                        .addComponent(jtextfield4Search))
        );
        layoutSearch.setHorizontalGroup(layoutSearch.createParallelGroup()
                .addGroup(layoutSearch.createSequentialGroup()
                        .addComponent(jbuttonAddres4Search)
                        .addComponent(jtextfieldAddres4Search))
                .addComponent(jprogressBar4Search)
                .addComponent(jlabelAddres4Search)
                .addComponent(jslider4Search)
                .addGroup(layoutSearch.createSequentialGroup()
                        .addComponent(jcheckbox4Search)
                        .addComponent(jlabelThreshold4Search))
                .addGroup(layoutSearch.createSequentialGroup()
                        .addComponent(jlabelAntennaPower4Search)
                        .addComponent(jtextfield4Search))
        );
        layoutSearch.setAutoCreateGaps(true);
        layoutSearch.setAutoCreateContainerGaps(true);

        jpanelSearch.setLayout(layoutSearch);
        jpanelSearch.setVisible(true);
        return jpanelSearch;
    }

    void showListFrame(JTextField jtextField) {
        if (showingListFrame)   return;
        if (defTableModelInventoryRfid.getRowCount() < 0) return;

        JFrame jframeList = new JFrame("Tag List");
        jframeList.setLocationRelativeTo( null );
        jframeList.setMinimumSize(new Dimension(400,300));
        jframeList.setDefaultCloseOperation(jframeList.DISPOSE_ON_CLOSE);
        jframeList.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                System.out.println("CLOSE is pressed");
                closeListFrame();
                super.windowClosing(windowEvent);
            }
        });

        JTable jtable = new JTable(defTableModelInventoryRfid) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                int rendererWidth = component.getPreferredSize().width;
                TableColumn tableColumn = getColumnModel().getColumn(column);
                tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
                return component;
            }
        };
        jtable.setRowHeight(itableRowHeight);
        jtable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);   //AUTO_RESIZE_OFF, AUTO_RESIZE_ALL_COLUMNS
        jtable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jtable.setDragEnabled(false);

        JScrollPane jscrollPane = new JScrollPane(jtable);
        jscrollPane.setMinimumSize(new Dimension(200, 100));
        jscrollPane.revalidate ();

        JPanel jpanelControls = getPanelControls(jframeList, "List");

        GroupLayout layout = new GroupLayout(jframeList.getContentPane());
        layout.setVerticalGroup(layout.createParallelGroup()
                .addComponent(jscrollPane)
                .addComponent(jpanelControls)
        );
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addComponent(jscrollPane)
                .addComponent(jpanelControls)
        );
        jframeList.setLayout(layout);

        jframeList.pack();
        jframeList.setVisible(true);

        showingListFrame = true; jtextFieldList = jtextField; jtableList = jtable;
    }

    boolean showingListFrame;
    JTextField jtextFieldList; JTable jtableList;
    void closeListFrame() {
        showingListFrame = false;
        String address = null;
        int selectedRow = jtableList.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                Object selectedObject = defTableModelInventoryRfid.getValueAt(jtableList.getSelectedRow(), 2);
                address = selectedObject.toString();
            } catch (Exception ex) { }
        }
        if (address == null) {
            System.out.println("No selected Device"); return;
        } else {
            jtextFieldList.setText(address);
        }
    }

    JTextField jtextfieldAntennaCycle4Setting, jtextfieldAntennaDwell4Setting, jtextfieldAntennaPower4Setting;
    JComboBox jcomboboxQueryTarget4Setting, jcomboxQuerySession4Setting, jcomboxQuerySelect4Setting; JCheckBox jcheckboxQueryAlgorithm4Setting; JTextField jtextfieldQueryProfile4Setting;
    JTextField jtextfieldDynamicStartQ4Setting, jtextfieldDynamicMaxQ4Setting, jtextfieldDynamicMinQ4Setting, jtextfieldDynamicRetry4Setting;
    JTextField jtextfieldFixedQ4Setting, jtextfieldFixedRetry4Setting; JCheckBox jcheckboxRunTilZero4Setting;
    JPanel getPanelSetting() {
        JLabel jlabelAntennaCycle4Setting = new JLabel("Antenna cycle(0-65535)");
        JLabel jlabelAntennaDwell4Setting = new JLabel("Antenna dwell(0-65535ms)");
        JLabel jlabelAntennaPower4Setting = new JLabel("Antenna power(0-30dBm)");
        JLabel jlabelQueryTarget4Setting = new JLabel("Query target");
        JLabel jlabelQuerySession4Setting = new JLabel("Query session");
        JLabel jlabelQuerySelect4Setting = new JLabel("Query select");
        JLabel jlabelQueryAlgorithm4Setting = new JLabel("Algorithm dynamic (not fixed)");
        JLabel jlabelQueryProfile4Setting = new JLabel("Profile(0-3)");

        JLabel jlabelDynamicAlgo4Setting = new JLabel("Dynamic Algorithm:");
        JLabel jlabelFixedAlgo4Setting = new JLabel("Fixed Algorithm:");

        JLabel jlabelDynamicStartQ4Setting = new JLabel("Start Q(0-15)");
        JLabel jlabelDynamicMaxQ4Setting = new JLabel("Maximum Q(0-15)");
        JLabel jlabelDynamicMinQ4Setting = new JLabel("Minimum Q(0-15)");
        JLabel jlabelDynamicRetry4Setting = new JLabel("Retry(0-255)");
        JLabel jlabelFixedQ4Setting = new JLabel("Q Value(0-15)");
        JLabel jlabelFixedRetry4Setting = new JLabel("Retry(0-255)");
        JLabel jlabelRunTilZero4Setting = new JLabel("Run til zero");

        jtextfieldAntennaCycle4Setting = new JTextField();
        jtextfieldAntennaCycle4Setting.setMaximumSize(new Dimension(textfieldWidth,textfieldHeight));
        jtextfieldAntennaDwell4Setting = new JTextField();
        jtextfieldAntennaDwell4Setting.setMaximumSize(new Dimension(textfieldWidth,textfieldHeight));
        jtextfieldAntennaPower4Setting= new JTextField();
        jtextfieldAntennaPower4Setting.setMaximumSize(new Dimension(textfieldWidth,textfieldHeight));

        String[] strQueryTargetType = { "Target A", "Target B", "Target A/B alternative"};
        String[] strQuerySessionType = { "Inventoried S0", "Inventoried S1", "Inventoried S2", "Inventoried S3"};
        String[] strQuerySelectType = { "All", "All", "~SL", "SL"};

        jcomboboxQueryTarget4Setting = new JComboBox(strQueryTargetType);
        jcomboboxQueryTarget4Setting.setMaximumSize(new Dimension(textfieldWidth,textfieldHeight));
        jcomboxQuerySession4Setting = new JComboBox(strQuerySessionType);
        jcomboxQuerySession4Setting.setMaximumSize(new Dimension(textfieldWidth,textfieldHeight));
        jcomboxQuerySelect4Setting = new JComboBox(strQuerySelectType);
        jcomboxQuerySelect4Setting.setMaximumSize(new Dimension(textfieldWidth,textfieldHeight));
        jcomboxQuerySelect4Setting.setEnabled(false);
        jcheckboxQueryAlgorithm4Setting = new JCheckBox();
        jtextfieldQueryProfile4Setting = new JTextField();
        jtextfieldQueryProfile4Setting.setMaximumSize(new Dimension(textfieldWidth,textfieldHeight));

        jtextfieldDynamicStartQ4Setting = new JTextField();
        jtextfieldDynamicStartQ4Setting.setMaximumSize(new Dimension(textfieldWidth,textfieldHeight));
        jtextfieldDynamicMaxQ4Setting = new JTextField();
        jtextfieldDynamicMaxQ4Setting.setMaximumSize(new Dimension(textfieldWidth,textfieldHeight));
        jtextfieldDynamicMinQ4Setting = new JTextField();
        jtextfieldDynamicMinQ4Setting.setMaximumSize(new Dimension(textfieldWidth,textfieldHeight));
        jtextfieldDynamicRetry4Setting = new JTextField();
        jtextfieldDynamicRetry4Setting.setMaximumSize(new Dimension(textfieldWidth,textfieldHeight));
        jtextfieldFixedQ4Setting = new JTextField();
        jtextfieldFixedQ4Setting.setMaximumSize(new Dimension(textfieldWidth,textfieldHeight));
        jtextfieldFixedRetry4Setting = new JTextField();
        jtextfieldFixedRetry4Setting.setMaximumSize(new Dimension(textfieldWidth,textfieldHeight));
        jcheckboxRunTilZero4Setting = new JCheckBox();

        JPanel jpanelSetting = new JPanel();

        GroupLayout layoutSetting = new GroupLayout(jpanelSetting);
        layoutSetting.setVerticalGroup(layoutSetting.createSequentialGroup()
                .addGroup(layoutSetting.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(jlabelAntennaCycle4Setting)
                        .addComponent(jlabelQueryTarget4Setting)
                        .addComponent(jtextfieldAntennaCycle4Setting)
                        .addComponent(jcomboboxQueryTarget4Setting))
                .addGroup(layoutSetting.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(jlabelAntennaDwell4Setting)
                        .addComponent(jlabelQuerySession4Setting)
                        .addComponent(jtextfieldAntennaDwell4Setting)
                        .addComponent(jcomboxQuerySession4Setting))
                .addGroup(layoutSetting.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(jlabelAntennaPower4Setting)
                        .addComponent(jlabelQuerySelect4Setting)
                        .addComponent(jtextfieldAntennaPower4Setting)
                        .addComponent(jcomboxQuerySelect4Setting))
                .addGroup(layoutSetting.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(jlabelQueryAlgorithm4Setting)
                        .addComponent(jcheckboxQueryAlgorithm4Setting))
                .addGroup(layoutSetting.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(jlabelQueryProfile4Setting)
                        .addComponent(jtextfieldQueryProfile4Setting))
                .addComponent(jlabelDynamicAlgo4Setting)
                .addGroup(layoutSetting.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(jlabelDynamicStartQ4Setting)
                        .addComponent(jtextfieldDynamicStartQ4Setting)
                        .addComponent(jlabelFixedAlgo4Setting))
                .addGroup(layoutSetting.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(jlabelDynamicMaxQ4Setting)
                        .addComponent(jtextfieldDynamicMaxQ4Setting)
                        .addComponent(jlabelFixedQ4Setting)
                        .addComponent(jtextfieldFixedQ4Setting))
                .addGroup(layoutSetting.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(jlabelDynamicMinQ4Setting)
                        .addComponent(jtextfieldDynamicMinQ4Setting)
                        .addComponent(jlabelFixedRetry4Setting)
                        .addComponent(jtextfieldFixedRetry4Setting))
                .addGroup(layoutSetting.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(jlabelDynamicRetry4Setting)
                        .addComponent(jtextfieldDynamicRetry4Setting)
                        .addComponent(jlabelRunTilZero4Setting)
                        .addComponent(jcheckboxRunTilZero4Setting))
        );
        layoutSetting.setHorizontalGroup(layoutSetting.createSequentialGroup()
                .addGroup(layoutSetting.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(jlabelAntennaCycle4Setting)
                        .addComponent(jlabelAntennaDwell4Setting)
                        .addComponent(jlabelAntennaPower4Setting)
                        .addComponent(jlabelDynamicAlgo4Setting)
                        .addComponent(jlabelDynamicStartQ4Setting)
                        .addComponent(jlabelDynamicMaxQ4Setting)
                        .addComponent(jlabelDynamicMinQ4Setting)
                        .addComponent(jlabelDynamicRetry4Setting))
                .addGroup(layoutSetting.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jtextfieldAntennaCycle4Setting)
                        .addComponent(jtextfieldAntennaDwell4Setting)
                        .addComponent(jtextfieldAntennaPower4Setting)
                        .addComponent(jtextfieldDynamicStartQ4Setting)
                        .addComponent(jtextfieldDynamicMaxQ4Setting)
                        .addComponent(jtextfieldDynamicMinQ4Setting)
                        .addComponent(jtextfieldDynamicRetry4Setting))
                .addGroup(layoutSetting.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(jlabelQueryTarget4Setting)
                        .addComponent(jlabelQuerySession4Setting)
                        .addComponent(jlabelQuerySelect4Setting)
                        .addComponent(jlabelQueryAlgorithm4Setting)
                        .addComponent(jlabelQueryProfile4Setting)
                        .addComponent(jlabelFixedAlgo4Setting)
                        .addComponent(jlabelFixedQ4Setting)
                        .addComponent(jlabelFixedRetry4Setting)
                        .addComponent(jlabelRunTilZero4Setting))
                .addGroup(layoutSetting.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jcomboboxQueryTarget4Setting)
                        .addComponent(jcomboxQuerySession4Setting)
                        .addComponent(jcomboxQuerySelect4Setting)
                        .addComponent(jcheckboxQueryAlgorithm4Setting)
                        .addComponent(jtextfieldQueryProfile4Setting)
                        .addComponent(jtextfieldFixedQ4Setting)
                        .addComponent(jtextfieldFixedRetry4Setting)
                        .addComponent(jcheckboxRunTilZero4Setting))
        );
        layoutSetting.setAutoCreateGaps(true);
        layoutSetting.setAutoCreateContainerGaps(true);

        jpanelSetting.setLayout(layoutSetting);
        jpanelSetting.setVisible(true);
        return jpanelSetting;
    }

    void showAllSetting() {
        jtextfieldAntennaCycle4Setting.setText(String.valueOf(cs108javalibrary.getAntennaCycle()));
        jtextfieldAntennaDwell4Setting.setText(String.valueOf(cs108javalibrary.getAntennaDwell()));
        jtextfieldAntennaPower4Setting.setText(String.valueOf(cs108javalibrary.getPwrlevel()));

        jcomboboxQueryTarget4Setting.setSelectedIndex(cs108javalibrary.getQueryTarget());
        jcomboxQuerySession4Setting.setSelectedIndex(cs108javalibrary.getQuerySession());
        jcomboxQuerySelect4Setting.setSelectedIndex(cs108javalibrary.getQuerySelect());
        jcheckboxQueryAlgorithm4Setting.setSelected(cs108javalibrary.getInvAlgo());
        jtextfieldQueryProfile4Setting.setText(String.valueOf(cs108javalibrary.getCurrentProfile()));

        jtextfieldDynamicStartQ4Setting.setText(String.valueOf(cs108javalibrary.getStartQValue()));
        jtextfieldDynamicMaxQ4Setting.setText(String.valueOf(cs108javalibrary.getMaxQValue()));
        jtextfieldDynamicMinQ4Setting.setText(String.valueOf(cs108javalibrary.getMinQValue()));
        jtextfieldDynamicRetry4Setting.setText(String.valueOf(cs108javalibrary.getRetryCount()));

        jtextfieldFixedQ4Setting.setText(String.valueOf(cs108javalibrary.getFixedQValue()));
        jtextfieldFixedRetry4Setting.setText(String.valueOf(cs108javalibrary.getFixedRetryCount()));
        jcheckboxRunTilZero4Setting.setSelected(cs108javalibrary.getRepeatUnitNoTags());
    }

    JTextField jtextfieldMaskData4PreFilter, jtextfieldOffset4PreFilter; JComboBox jcomboboxMemoryBank4PreFilter, jcomboboxActio4PreFilter, jcomboboxTarget4PreFilter;
    JCheckBox jcheckboxEnable4PreFilter; JTextField jtextfieldIndex4PreFilter;
    JTextField jtextfieldMaskData4PostFilter, jtextfieldOffset4PostFilter;
    JCheckBox jcheckboxMatchEPC4PosFilter, jcheckboxEnable4PostFilter;
    JPanel getPanelFilter() {
        JLabel jlabelPre4Filter = new JLabel("Pre-Filter:");
        JLabel jlabelPost4Filter = new JLabel("Post-Filter:");

        JButton jbuttonMaskData4PreFilter = new JButton("Select Mask data");
        jbuttonMaskData4PreFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showListFrame(jtextfieldMaskData4PreFilter);
            }
        });
//        JLabel jlabelMaskData4PreFilter = new JLabel("Mask data");

        JLabel jlabelOffset4PreFilter = new JLabel("Offset (bits)");
        JLabel jlabelMemoryBank4PreFilter = new JLabel("Memory Bank");
        JLabel jlabelActio4PreFilter = new JLabel("Action");
        JLabel jlabelTarget4PreFilter = new JLabel("Target");
        JLabel jlabelEnable4PreFilter = new JLabel("Enable");
        JLabel jlabelIndex4PreFilter = new JLabel("Index(0-3)");

        JButton jbuttonMaskData4PostFilter = new JButton("Select Mask data");
        jbuttonMaskData4PostFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showListFrame(jtextfieldMaskData4PostFilter);
            }
        });
//        JLabel jlabelMaskData4PostFilter = new JLabel("Mask data");

        JLabel jlabelOffset4PostFilter = new JLabel("Offset (bits)");
        JLabel jlabelMatchEPC4PosFilter = new JLabel("match on ~EPC (not EPC)");
        JLabel jlabelEnable4PostFilter = new JLabel("Enable");

        jtextfieldMaskData4PreFilter = new JTextField();
        jtextfieldMaskData4PreFilter.setMaximumSize(new Dimension(textfieldEpcWidth,textfieldHeight));
        jtextfieldOffset4PreFilter = new JTextField();
        jtextfieldOffset4PreFilter.setMaximumSize(new Dimension(textfieldWidth,textfieldHeight));
        jcheckboxEnable4PreFilter = new JCheckBox();

        String[] strMemoryBankType = { "Reserved", "EPC", "TID", "User"};
        String[] strActionType = { "Match(assert SL), else(dessert SL)", 
            "Match(assert SL), else(do nothing)", 
            "Match(do nothing), else(dessert SL)", 
            "Match(invert SL), else(do nothing)", 
            "Match(dessert SL), else(assert SL)", 
            "Match(dessert SL), else(do nothing)", 
            "Match(do nothing), else(assert SL)", 
            "Match(do nothing), else(invert SL)"
        };
        String[] strTargetType = { "Inventoried S0", "Inventoried S1", "Inventoried S2", "Inventoried S3", "SL"};

        jcomboboxMemoryBank4PreFilter = new JComboBox(strMemoryBankType);
        jcomboboxMemoryBank4PreFilter.setMaximumSize(new Dimension(textfieldWidth,textfieldHeight));
        jcomboboxMemoryBank4PreFilter.setEnabled(false);
        jcomboboxActio4PreFilter = new JComboBox(strActionType);
        jcomboboxActio4PreFilter.setMaximumSize(new Dimension(textfieldWidth,textfieldHeight));
        jcomboboxActio4PreFilter.setEnabled(false);
        jcomboboxTarget4PreFilter = new JComboBox(strTargetType);
        jcomboboxTarget4PreFilter.setMaximumSize(new Dimension(textfieldWidth,textfieldHeight));
        jcomboboxTarget4PreFilter.setEnabled(false);

        jtextfieldIndex4PreFilter = new JTextField();
        jtextfieldIndex4PreFilter.setMaximumSize(new Dimension(textfieldWidth,textfieldHeight));

        jtextfieldMaskData4PostFilter = new JTextField();
        jtextfieldMaskData4PostFilter.setMaximumSize(new Dimension(textfieldEpcWidth,textfieldHeight)); 
        jtextfieldOffset4PostFilter = new JTextField();
        jtextfieldOffset4PostFilter.setMaximumSize(new Dimension(textfieldWidth,textfieldHeight));
        jcheckboxMatchEPC4PosFilter = new JCheckBox();
        jcheckboxEnable4PostFilter = new JCheckBox();

        JPanel jpanelFilter = new JPanel();

        GroupLayout layoutFilter = new GroupLayout(jpanelFilter);
        layoutFilter.setVerticalGroup(layoutFilter.createSequentialGroup()
                .addGroup(layoutFilter.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(jlabelPre4Filter)
                        .addComponent(jlabelPost4Filter))
                .addGroup(layoutFilter.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(jbuttonMaskData4PreFilter)    //.addComponent(jlabelMaskData4PreFilter)
                        .addComponent(jtextfieldMaskData4PreFilter)
                        .addComponent(jbuttonMaskData4PostFilter)   //jlabelMaskData4PostFilter)
                        .addComponent(jtextfieldMaskData4PostFilter))
                .addGroup(layoutFilter.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(jlabelOffset4PreFilter)
                        .addComponent(jtextfieldOffset4PreFilter)
                        .addComponent(jlabelOffset4PostFilter)
                        .addComponent(jtextfieldOffset4PostFilter))
                .addGroup(layoutFilter.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(jlabelMemoryBank4PreFilter)
                        .addComponent(jcomboboxMemoryBank4PreFilter)
                        .addComponent(jlabelMatchEPC4PosFilter)
                        .addComponent(jcheckboxMatchEPC4PosFilter))
                .addGroup(layoutFilter.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(jlabelActio4PreFilter)
                        .addComponent(jcomboboxActio4PreFilter)
                        .addComponent(jlabelEnable4PostFilter)
                        .addComponent(jcheckboxEnable4PostFilter))
                .addGroup(layoutFilter.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(jlabelTarget4PreFilter)
                        .addComponent(jcomboboxTarget4PreFilter))
                .addGroup(layoutFilter.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(jlabelEnable4PreFilter)
                        .addComponent(jcheckboxEnable4PreFilter))
                .addGroup(layoutFilter.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(jlabelIndex4PreFilter)
                        .addComponent(jtextfieldIndex4PreFilter))
        );
        layoutFilter.setHorizontalGroup(layoutFilter.createSequentialGroup()
                .addGroup(layoutFilter.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(jlabelPre4Filter)
                        .addComponent(jbuttonMaskData4PreFilter)    //.addComponent(jlabelMaskData4PreFilter)
                        .addComponent(jlabelOffset4PreFilter)
                        .addComponent(jlabelMemoryBank4PreFilter)
                        .addComponent(jlabelActio4PreFilter)
                        .addComponent(jlabelTarget4PreFilter)
                        .addComponent(jlabelEnable4PreFilter)
                        .addComponent(jlabelIndex4PreFilter))
                .addGroup(layoutFilter.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jtextfieldMaskData4PreFilter)
                        .addComponent(jtextfieldOffset4PreFilter)
                        .addComponent(jcomboboxMemoryBank4PreFilter)
                        .addComponent(jcomboboxActio4PreFilter)
                        .addComponent(jcomboboxTarget4PreFilter)
                        .addComponent(jcheckboxEnable4PreFilter)
                        .addComponent(jtextfieldIndex4PreFilter))
                .addGroup(layoutFilter.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(jlabelPost4Filter)
                        .addComponent(jbuttonMaskData4PostFilter)   //jlabelMaskData4PostFilter)
                        .addComponent(jlabelOffset4PostFilter)
                        .addComponent(jlabelMatchEPC4PosFilter)
                        .addComponent(jlabelEnable4PostFilter))
                .addGroup(layoutFilter.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jtextfieldMaskData4PostFilter)
                        .addComponent(jtextfieldOffset4PostFilter)
                        .addComponent(jcheckboxMatchEPC4PosFilter)
                        .addComponent(jcheckboxEnable4PostFilter))
        );
        layoutFilter.setAutoCreateGaps(true);
        layoutFilter.setAutoCreateContainerGaps(true);

        jpanelFilter.setLayout(layoutFilter);
        jpanelFilter.setVisible(true);
        return jpanelFilter;
    }

    void showAllFilter() {
        jtextfieldIndex4PreFilter.setText(String.valueOf(cs108javalibrary.getInvSelectIndex()));
        jcheckboxEnable4PreFilter.setSelected(cs108javalibrary.getSelectEnable());
        jcomboboxTarget4PreFilter.setSelectedIndex(cs108javalibrary.getSelectTarget());
        jcomboboxActio4PreFilter.setSelectedIndex(cs108javalibrary.getSelectAction());
        jcomboboxMemoryBank4PreFilter.setSelectedIndex(cs108javalibrary.getSelectMaskBank());
        int iOffset = cs108javalibrary.getSelectMaskOffset();
        if (iOffset < 32) iOffset = 32;
        iOffset -= 32;
        jtextfieldOffset4PreFilter.setText(String.valueOf(iOffset));
        jtextfieldMaskData4PreFilter.setText(cs108javalibrary.getSelectMaskData());

        jcheckboxEnable4PostFilter.setSelected(cs108javalibrary.getInvMatchEnable());
        jcheckboxMatchEPC4PosFilter.setSelected(cs108javalibrary.getInvMatchType());
        jtextfieldOffset4PostFilter.setText(String.valueOf(cs108javalibrary.getInvMatchOffset()));
        jtextfieldMaskData4PostFilter.setText(cs108javalibrary.getInvMatchData());
    }

    JComboBox jcomboboxMemoryBank4ReadWrite;
    JCheckBox jcheckboxWrite4ReadWrite;
    JCheckBox jcheckboxKillPwd4ReadWrite, jcheckboxAccessPwd4ReadWrite, jcheckboxPC4ReadWrite, jcheckboxEPC4ReadWrite;
    JCheckBox jcheckboxTIDData4ReadWrite, jcheckboxUSERData4ReadWrite, jcheckboxData4ReadWrite;
    JTextField jtextfield4ReadWrite, jtextfieldAddres4ReadWrite;
    JTextField jtextfieldTIDOffset4ReadWrite, jtextfieldTIDSize4ReadWrite, jtextfieldTIDData4ReadWrite;
    JTextField jtextfieldUSEROffset4ReadWrite, jtextfieldUSERSize4ReadWrite, jtextfieldUSERData4ReadWrite;
    JTextField jtextfieldOffset4ReadWrite, jtextfieldSize4ReadWrite, jtextfieldData4ReadWrite;
    JTextField jtextfieldKillPwd4ReadWrite, jtextfieldAccessPwd4ReadWrite, jtextfieldPC4ReadWrite, jtextfieldEPC4ReadWrite;
    JPanel getPanelReadWrite() {
        JButton jbuttonAddres4ReadWrite  = new JButton("Select Mask data");
        jbuttonAddres4ReadWrite.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showListFrame(jtextfieldAddres4ReadWrite);
            }
        });

        JLabel jlabelAntennaPower4ReadWrite = new JLabel("Antenna power(0-30dBm)");
        JLabel jlabelMemoryBank4ReadWrite = new JLabel("Memory Bank");
        JLabel jlabelTIDOffset4ReadWrite = new JLabel("TID Offset");
        JLabel jlabelUSEROffset4ReadWrite = new JLabel("USER Offset");
        JLabel jlabelOffset4ReadWrite = new JLabel("Offset(words)");
        JLabel jlabelTIDSize4ReadWrite = new JLabel("TID Size");
        JLabel jlabelUSERSize4ReadWrite = new JLabel("USER Size");
        JLabel jlabelSize4ReadWrite = new JLabel("Size(words)");

        String[] strMemoryBankType = { "Reserved", "EPC", "TID", "User"};
        jcomboboxMemoryBank4ReadWrite = new JComboBox(strMemoryBankType);
        jcomboboxMemoryBank4ReadWrite.setMaximumSize(new Dimension(textfieldWidth,textfieldHeight));

        jcheckboxWrite4ReadWrite = new JCheckBox("Write");
        jcheckboxKillPwd4ReadWrite = new JCheckBox("Kill Password");
        jcheckboxAccessPwd4ReadWrite = new JCheckBox("Access Password");
        jcheckboxPC4ReadWrite = new JCheckBox("PC");
        jcheckboxEPC4ReadWrite = new JCheckBox("EPC");
        jcheckboxTIDData4ReadWrite = new JCheckBox("TID Data");
        jcheckboxUSERData4ReadWrite = new JCheckBox("USER Data");
        jcheckboxData4ReadWrite = new JCheckBox("Data read/write");

        jtextfieldAddres4ReadWrite = new JTextField();
        jtextfieldAddres4ReadWrite.setMaximumSize(new Dimension(textfieldEpcWidth,textfieldHeight));
        jtextfield4ReadWrite = new JTextField();
        jtextfield4ReadWrite.setMaximumSize(new Dimension(textfieldWidth,textfieldHeight));

        jtextfieldKillPwd4ReadWrite = new JTextField();
        jtextfieldKillPwd4ReadWrite.setMaximumSize(new Dimension(textfieldWidth,textfieldHeight));
        jtextfieldAccessPwd4ReadWrite = new JTextField();
        jtextfieldAccessPwd4ReadWrite.setMaximumSize(new Dimension(textfieldWidth,textfieldHeight));

        jtextfieldPC4ReadWrite = new JTextField();
        jtextfieldPC4ReadWrite.setMaximumSize(new Dimension(textfieldWidth,textfieldHeight));
        jtextfieldEPC4ReadWrite = new JTextField();
        jtextfieldEPC4ReadWrite.setMaximumSize(new Dimension(textfieldEpcWidth,textfieldHeight));

        jtextfieldTIDOffset4ReadWrite = new JTextField();
        jtextfieldTIDOffset4ReadWrite.setMaximumSize(new Dimension(textfieldWidth,textfieldHeight));
        jtextfieldTIDSize4ReadWrite = new JTextField();
        jtextfieldTIDSize4ReadWrite.setMaximumSize(new Dimension(textfieldWidth,textfieldHeight));
        jtextfieldTIDData4ReadWrite = new JTextField();
        jtextfieldTIDData4ReadWrite.setMaximumSize(new Dimension(textfieldEpcWidth,textfieldHeight));

        jtextfieldUSEROffset4ReadWrite = new JTextField();
        jtextfieldUSEROffset4ReadWrite.setMaximumSize(new Dimension(textfieldWidth,textfieldHeight));
        jtextfieldUSERSize4ReadWrite = new JTextField();
        jtextfieldUSERSize4ReadWrite.setMaximumSize(new Dimension(textfieldWidth,textfieldHeight));
        jtextfieldUSERData4ReadWrite = new JTextField();
        jtextfieldUSERData4ReadWrite.setMaximumSize(new Dimension(textfieldEpcWidth,textfieldHeight));

        jtextfieldOffset4ReadWrite = new JTextField();
        jtextfieldOffset4ReadWrite.setMaximumSize(new Dimension(textfieldWidth,textfieldHeight));
        jtextfieldSize4ReadWrite = new JTextField();
        jtextfieldSize4ReadWrite.setMaximumSize(new Dimension(textfieldWidth,textfieldHeight));
        jtextfieldData4ReadWrite = new JTextField();
        jtextfieldData4ReadWrite.setMaximumSize(new Dimension(textfieldEpcWidth,textfieldHeight));

        JPanel jpanelReadWrite = new JPanel();

        GroupLayout layoutReadWrite = new GroupLayout(jpanelReadWrite);
        layoutReadWrite.setVerticalGroup(layoutReadWrite.createSequentialGroup()
                .addGroup(layoutReadWrite.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(jbuttonAddres4ReadWrite)
                        .addComponent(jtextfieldAddres4ReadWrite)
                        .addComponent(jlabelAntennaPower4ReadWrite)
                        .addComponent(jtextfield4ReadWrite)
                        .addComponent(jcheckboxWrite4ReadWrite))
                .addGroup(layoutReadWrite.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(jcheckboxKillPwd4ReadWrite)
                        .addComponent(jtextfieldKillPwd4ReadWrite)
                        .addComponent(jcheckboxAccessPwd4ReadWrite)
                        .addComponent(jtextfieldAccessPwd4ReadWrite))
                .addGroup(layoutReadWrite.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(jcheckboxPC4ReadWrite)
                        .addComponent(jtextfieldPC4ReadWrite)
                        .addComponent(jcheckboxEPC4ReadWrite)
                        .addComponent(jtextfieldEPC4ReadWrite))
                .addGroup(layoutReadWrite.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(jlabelTIDOffset4ReadWrite)
                        .addComponent(jtextfieldTIDOffset4ReadWrite)
                        .addComponent(jlabelTIDSize4ReadWrite)
                        .addComponent(jtextfieldTIDSize4ReadWrite)
                        .addComponent(jcheckboxTIDData4ReadWrite)
                        .addComponent(jtextfieldTIDData4ReadWrite))
                .addGroup(layoutReadWrite.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(jlabelUSEROffset4ReadWrite)
                        .addComponent(jtextfieldUSEROffset4ReadWrite)
                        .addComponent(jlabelUSERSize4ReadWrite)
                        .addComponent(jtextfieldUSERSize4ReadWrite)
                        .addComponent(jcheckboxUSERData4ReadWrite)
                        .addComponent(jtextfieldUSERData4ReadWrite))
                .addGroup(layoutReadWrite.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(jlabelMemoryBank4ReadWrite)
                        .addComponent(jcomboboxMemoryBank4ReadWrite))
                .addGroup(layoutReadWrite.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(jlabelOffset4ReadWrite)
                        .addComponent(jtextfieldOffset4ReadWrite)
                        .addComponent(jlabelSize4ReadWrite)
                        .addComponent(jtextfieldSize4ReadWrite)
                        .addComponent(jcheckboxData4ReadWrite)
                        .addComponent(jtextfieldData4ReadWrite)) 
        );
        layoutReadWrite.setHorizontalGroup(layoutReadWrite.createSequentialGroup()
                .addGroup(layoutReadWrite.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jlabelAntennaPower4ReadWrite)
                        .addComponent(jcheckboxKillPwd4ReadWrite)
                        .addComponent(jcheckboxPC4ReadWrite)
                        .addComponent(jlabelMemoryBank4ReadWrite)
                        .addGroup(layoutReadWrite.createSequentialGroup()
                                .addComponent(jlabelTIDOffset4ReadWrite)
                                .addComponent(jtextfieldTIDOffset4ReadWrite))
                        .addGroup(layoutReadWrite.createSequentialGroup()
                                .addComponent(jlabelUSEROffset4ReadWrite)
                                .addComponent(jtextfieldUSEROffset4ReadWrite))
                        .addGroup(layoutReadWrite.createSequentialGroup()
                                .addComponent(jlabelOffset4ReadWrite)
                                .addComponent(jtextfieldOffset4ReadWrite)))
                .addGroup(layoutReadWrite.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layoutReadWrite.createSequentialGroup()
                                .addComponent(jtextfield4ReadWrite)
                                .addComponent(jcheckboxWrite4ReadWrite))
                        .addComponent(jtextfieldKillPwd4ReadWrite)
                        .addComponent(jtextfieldPC4ReadWrite)
                        .addComponent(jcomboboxMemoryBank4ReadWrite)
                        .addGroup(layoutReadWrite.createSequentialGroup()
                                .addComponent(jlabelTIDSize4ReadWrite)
                                .addComponent(jtextfieldTIDSize4ReadWrite))
                        .addGroup(layoutReadWrite.createSequentialGroup()
                                .addComponent(jlabelUSERSize4ReadWrite)
                                .addComponent(jtextfieldUSERSize4ReadWrite))
                        .addGroup(layoutReadWrite.createSequentialGroup()
                                .addComponent(jlabelSize4ReadWrite)
                                .addComponent(jtextfieldSize4ReadWrite))
                        )
                .addGroup(layoutReadWrite.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jbuttonAddres4ReadWrite)
                        .addComponent(jcheckboxAccessPwd4ReadWrite)
                        .addComponent(jcheckboxEPC4ReadWrite)
                        .addComponent(jcheckboxTIDData4ReadWrite)
                        .addComponent(jcheckboxUSERData4ReadWrite)
                        .addComponent(jcheckboxData4ReadWrite))
                .addGroup(layoutReadWrite.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jtextfieldAddres4ReadWrite)
                        .addComponent(jtextfieldAccessPwd4ReadWrite)
                        .addComponent(jtextfieldEPC4ReadWrite)
                        .addComponent(jtextfieldTIDData4ReadWrite)
                        .addComponent(jtextfieldUSERData4ReadWrite)
                        .addComponent(jtextfieldData4ReadWrite))
        );
        layoutReadWrite.setAutoCreateGaps(true);
        layoutReadWrite.setAutoCreateContainerGaps(true);

        jpanelReadWrite.setLayout(layoutReadWrite);
        jpanelReadWrite.setVisible(true);
        return jpanelReadWrite;
    }

    boolean readwriting = false;
    void readwriteHandler() {
        if (isConnected == false) {
            jtextArea.append("\nNo connection. Cannot read/write"); return;
        }
        if (readwriting == false && readwriteRunning == false) {
            String readwritingAddress = null;
            int powerLevel = 300;
            try {
                readwritingAddress = jtextfieldAddres4ReadWrite.getText();
                powerLevel = Integer.parseInt(jtextfield4ReadWrite.getText());
            } catch (Exception ex) {
            }
            System.out.println("readwriteHandler(): the selected string = " + readwritingAddress + ", powerLevel = " + powerLevel);
            if (readwritingAddress == null) return;
            if (readwritingAddress.length() == 0)   return;
            if (cs108javalibrary.setSelectedTag(readwritingAddress, powerLevel, false) == false) return;

            readwriting = true;
            Thread thread1 = new Thread(runnableReadwrite);
            thread1.start();
            setVisibleConnectOperating();
            jtabbedPane.setSelectedIndex(6); jbuttonReadWrite.setText(readwriteStopText); jbuttonReadWrite.setVisible(true);
        } else {
            readwriting = false;
            System.out.println("readwriteHandler(): readwriting = " + readwriting);
            cs108javalibrary.abortOperation();
        }
    }

    enum Check4ReadWrite { NULL, KILL, ACCESS, PC, EPC, TID, USER, OTHER, END }
    boolean readwriteRunning = false;
    Runnable runnableReadwrite = new Runnable() {
        @Override
        public void run() {
            readwriteRunning = true;

            long timeMillis = System.currentTimeMillis();
            boolean ending = false;
            boolean timeout = false;
            CS108JavaLibrary.Rx000pkgData rx000pkgData;

            Check4ReadWrite checkpoint = Check4ReadWrite.NULL; boolean startedOperation = false;
            boolean writeOperating = false;
            cs108javalibrary.appendToLog("runnableReadwrite(): enter the loop, lastCommandDuration = " + cs108javalibrary.getMacLastCommandDuration(false));
            while (cs108javalibrary.isBleConnected() && ending == false && timeout == false && isConnected && readwriting) {
                if (startedOperation == false) {
                    if (checkpoint == Check4ReadWrite.NULL) checkpoint = Check4ReadWrite.KILL;
                    else if (checkpoint == Check4ReadWrite.KILL) checkpoint = Check4ReadWrite.ACCESS;
                    else if (checkpoint == Check4ReadWrite.ACCESS) checkpoint = Check4ReadWrite.PC;
                    else if (checkpoint == Check4ReadWrite.PC) checkpoint = Check4ReadWrite.EPC;
                    else if (checkpoint == Check4ReadWrite.EPC) checkpoint = Check4ReadWrite.TID;
                    else if (checkpoint == Check4ReadWrite.TID) checkpoint = Check4ReadWrite.USER;
                    else if (checkpoint == Check4ReadWrite.USER) checkpoint = Check4ReadWrite.OTHER;
                    else    ending = true;

                    int accessBank = 1, accOffset = 0, accSize = 4; boolean ready = false;
                    if (checkpoint == Check4ReadWrite.KILL) {
                        if (jcheckboxKillPwd4ReadWrite.isSelected()) {
                            accessBank = 0; accOffset = 0; accSize = 2;
                            ready = true;
                        }
                    } else if (checkpoint == Check4ReadWrite.ACCESS) {
                        if (jcheckboxAccessPwd4ReadWrite.isSelected()) {
                            accessBank = 0; accOffset = 2; accSize = 2;
                            ready = true;
                        }
                    } else if (checkpoint == Check4ReadWrite.PC) {
                        if (jcheckboxPC4ReadWrite.isSelected()) {
                            accessBank = 1; accOffset = 1; accSize = 1;
                            ready = true;
                        }
                    } else if (checkpoint == Check4ReadWrite.EPC) {
                        if (jcheckboxEPC4ReadWrite.isSelected()) {
                            accessBank = 1;
                            accOffset = 2;
                            accSize = 6;
                            ready = true;
                        }
                    } else if (checkpoint == Check4ReadWrite.TID) {
                        try {
                            if (jcheckboxTIDData4ReadWrite.isSelected()) {
                                accessBank = 2;
                                accOffset = Integer.valueOf(jtextfieldTIDOffset4ReadWrite.getText());
                                accSize = Integer.valueOf(jtextfieldTIDSize4ReadWrite.getText());
                                ready = true;
                            }
                        } catch (Exception ex) { }
                    } else if (checkpoint == Check4ReadWrite.USER) {
                        try {
                            if (jcheckboxUSERData4ReadWrite.isSelected()) {
                                accessBank = 3;
                                accOffset = Integer.valueOf(jtextfieldUSEROffset4ReadWrite.getText());
                                accSize = Integer.valueOf(jtextfieldUSERSize4ReadWrite.getText());
                                ready = true;
                            }
                        } catch (Exception ex) { }
                    } else if (checkpoint == Check4ReadWrite.OTHER) {
                        try {
                            if (jcheckboxData4ReadWrite.isSelected()) {
                                accessBank = jcomboboxMemoryBank4ReadWrite.getSelectedIndex();
                                accOffset = Integer.valueOf(jtextfieldOffset4ReadWrite.getText());
                                accSize = Integer.valueOf(jtextfieldSize4ReadWrite.getText());
                                ready = true;
                            }
                        } catch (Exception ex) { }
                    }
                    if (ready) {
                        cs108javalibrary.mRfidDevice.mRx000Device.mRx000Setting.setAccessBank(accessBank);
                        cs108javalibrary.mRfidDevice.mRx000Device.mRx000Setting.setAccessOffset(accOffset);
                        cs108javalibrary.mRfidDevice.mRx000Device.mRx000Setting.setAccessCount(accSize);
                        if (jcheckboxWrite4ReadWrite.isSelected()) {
                            String strWriteData = "";
                            if (checkpoint == Check4ReadWrite.KILL) {
                                strWriteData = jtextfieldKillPwd4ReadWrite.getText();
                            } else if (checkpoint == Check4ReadWrite.ACCESS) {
                                strWriteData = jtextfieldAccessPwd4ReadWrite.getText();
                            } else if (checkpoint == Check4ReadWrite.PC) {
                                strWriteData = jtextfieldPC4ReadWrite.getText();
                            } else if (checkpoint == Check4ReadWrite.EPC) {
                                strWriteData = jtextfieldEPC4ReadWrite.getText();
                            } else if (checkpoint == Check4ReadWrite.TID) {
                                strWriteData = jtextfieldTIDData4ReadWrite.getText();
                            } else if (checkpoint == Check4ReadWrite.USER) {
                                strWriteData = jtextfieldUSERData4ReadWrite.getText();
                            } else if (checkpoint == Check4ReadWrite.OTHER) {
                                strWriteData = jtextfieldData4ReadWrite.getText();
                            }
                            jtextArea.append("\nstrWritData = " + strWriteData);
                            cs108javalibrary.mRfidDevice.mRx000Device.mRx000Setting.setAccessWriteData(strWriteData);
                            writeOperating = true;
                            cs108javalibrary.mRfidDevice.mRx000Device.sendHostRegRequestHST_CMD(CS108JavaLibrary.HostCommands.CMD_18K6CBLOCKWRITE);
                        } else {
                            writeOperating = false;
                            cs108javalibrary.mRfidDevice.mRx000Device.sendHostRegRequestHST_CMD(CS108JavaLibrary.HostCommands.CMD_18K6CREAD);
                        }
                        startedOperation = true;
                    }
                }
                rx000pkgData = cs108javalibrary.onRFIDEvent();
                if (cs108javalibrary.mrfidToWriteSize() != 0) {
                    try {
                        Thread.sleep(sleepInInventory);
                    } catch (InterruptedException ex) { }
                    timeMillis = System.currentTimeMillis();
                } else if (rx000pkgData != null) {
                    if (rx000pkgData.responseType == null) {
                        jtextArea.append("\nrunnableReadwrite(): null response");
                    } else if (rx000pkgData.responseType == CS108JavaLibrary.HostCmdResponseTypes.TYPE_18K6C_TAG_ACCESS) {
                        if (rx000pkgData.dataValues.length < 12) {
                            jtextArea.append("\nrunnableReadwrite(): invalid rx000pkgData.dataBytes = " + cs108javalibrary.byteArrayToString(rx000pkgData.dataValues));
                        } else {
                            int backscatterError = (rx000pkgData.dataValues[13 - 8] & 0xFF);
                            int macAccessError = (rx000pkgData.dataValues[14 - 8] & 0xFF) + (rx000pkgData.dataValues[15 - 8] & 0xFF) * 256;
                            int writtenLength = (rx000pkgData.dataValues[16 - 8] & 0xFF) + (rx000pkgData.dataValues[17 - 8] & 0xFF) * 256;
                            if (backscatterError == 0 && macAccessError == 0) {
                                byte[] dataRead = new byte[rx000pkgData.dataValues.length - 12];
                                System.arraycopy(rx000pkgData.dataValues, 12, dataRead, 0, dataRead.length);
                                if (writeOperating && rx000pkgData.dataValues[12 - 8] == (byte) 0xC7 && writtenLength != 0) {
                                    if (checkpoint == Check4ReadWrite.KILL) {
                                        jcheckboxKillPwd4ReadWrite.setSelected(false);
                                    } else if (checkpoint == Check4ReadWrite.ACCESS) {
                                        jcheckboxAccessPwd4ReadWrite.setSelected(false);
                                    } else if (checkpoint == Check4ReadWrite.PC) {
                                        jcheckboxPC4ReadWrite.setSelected(false);
                                    } else if (checkpoint == Check4ReadWrite.EPC) {
                                        jcheckboxEPC4ReadWrite.setSelected(false);
                                    } else if (checkpoint == Check4ReadWrite.TID) {
                                        jcheckboxTIDData4ReadWrite.setSelected(false);
                                    } else if (checkpoint == Check4ReadWrite.USER) {
                                        jcheckboxUSERData4ReadWrite.setSelected(false);
                                    } else if (checkpoint == Check4ReadWrite.OTHER) {
                                        jcheckboxData4ReadWrite.setSelected(false);
                                    }
                                } else if (writeOperating == false && rx000pkgData.dataValues[12 - 8] == (byte) 0xC2) {
                                    if (checkpoint == Check4ReadWrite.KILL) {
                                        jcheckboxKillPwd4ReadWrite.setSelected(false);
                                        jtextfieldKillPwd4ReadWrite.setText(cs108javalibrary.byteArrayToString(dataRead));
                                    } else if (checkpoint == Check4ReadWrite.ACCESS) {
                                        jcheckboxAccessPwd4ReadWrite.setSelected(false);
                                        jtextfieldAccessPwd4ReadWrite.setText(cs108javalibrary.byteArrayToString(dataRead));
                                    } else if (checkpoint == Check4ReadWrite.PC) {
                                        jcheckboxPC4ReadWrite.setSelected(false);
                                        jtextfieldPC4ReadWrite.setText(cs108javalibrary.byteArrayToString(dataRead));
                                    } else if (checkpoint == Check4ReadWrite.EPC) {
                                        jcheckboxEPC4ReadWrite.setSelected(false);
                                        jtextfieldEPC4ReadWrite.setText(cs108javalibrary.byteArrayToString(dataRead));
                                    } else if (checkpoint == Check4ReadWrite.TID) {
                                        jcheckboxTIDData4ReadWrite.setSelected(false);
                                        jtextfieldTIDData4ReadWrite.setText(cs108javalibrary.byteArrayToString(dataRead));
                                    } else if (checkpoint == Check4ReadWrite.USER) {
                                        jcheckboxUSERData4ReadWrite.setSelected(false);
                                        jtextfieldUSERData4ReadWrite.setText(cs108javalibrary.byteArrayToString(dataRead));
                                    } else if (checkpoint == Check4ReadWrite.OTHER) {
                                        jcheckboxData4ReadWrite.setSelected(false);
                                        jtextfieldData4ReadWrite.setText(cs108javalibrary.byteArrayToString(dataRead));
                                    }
                                }
                                System.out.println("\nrunnableReadwrite(): VALID rx000pkgData.dataBytes = " + cs108javalibrary.byteArrayToString(rx000pkgData.dataValues));
                            } else {
                                String err = "runnableReadwrite(): invalid rx000pkgData.dataBytes = " + cs108javalibrary.byteArrayToString(rx000pkgData.dataValues)
                                + ", backscatterError = " + backscatterError + ", macAccessError = " + macAccessError;
                                System.out.println(err);
                                jtextArea.append("\n" + err);
                            }
                        }
                    } else if (rx000pkgData.responseType == CS108JavaLibrary.HostCmdResponseTypes.TYPE_COMMAND_END) {
                        System.out.println("runnableReadwrite(): rx000pkgData.TYPE_COMMAND_END is received");
                        startedOperation = false;
                    }
                    else    System.out.println("runnableReadwrite(): invalid rx000pkgData.response = " + rx000pkgData.responseType.name() + " is received");
                }
                if (System.currentTimeMillis() - timeMillis > 10000) {
                    System.out.println("runnableReadwrite(): Exit as TIMEOUT");
                    timeout = true;
                }
            }
            System.out.println("runnableReadwrite(): exit the loop");
            if (cs108javalibrary.isBleConnected() && isConnected) {
                System.out.println("runnableReadwrite(): Ending ReadWrite");
                cs108javalibrary.getMacLastCommandDuration(true);
                cs108javalibrary.abortOperation();
                timeMillis = System.currentTimeMillis();
                while (cs108javalibrary.isBleConnected()) {
                    rx000pkgData = cs108javalibrary.onRFIDEvent();                    
                    if (cs108javalibrary.mrfidToWriteSize() == 0 && rx000pkgData == null) {
                        if (System.currentTimeMillis() - timeMillis > 2000) {
                            System.out.println("runnableReadwrite(): Exit as TIMEOUT");
                            break;
                        }
                    } else timeMillis = System.currentTimeMillis();
                }
                long lastCommandDuration = cs108javalibrary.getMacLastCommandDuration(false);
                cs108javalibrary.appendToLog("runnableReadwrite(): lastCommandDuration = " + lastCommandDuration);
                if (timeout) {
                    if (lastCommandDuration == 0)
                        System.out.println("runnableReadwrite(): Exit as RFID RESET");
                    else
                        System.out.println("runnableReadwrite(): Confirmed Exit as TIMEOUT");
                }
            }
            System.out.println("runnableReadwrite(): Ending finishes");

            readwriteRunning = false;
            jbuttonReadWrite.setText(readWriteStartText);
            setVisibleConnectStandby();
        }
    };

    JComboBox jcomboboxKill4Lock, jcomboboxAccess4Lock, jcomboboxEPC4Lock, jcomboboxTID4Lock, jcomboboxUSER4Lock;
    JCheckBox jcheckboxAccessPwd4Lock, jcheckboxKillPwd4Lock;
    JTextField jtextfield4Lock, jtextfieldAddres4Lock, jtextfieldKillPwd4Lock, jtextfieldAccessPwd4Lock;
    JPanel getPanelLock() {
        JButton jbuttonAddres4Lock = new JButton("Select Mask data");
        jbuttonAddres4Lock.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showListFrame(jtextfieldAddres4Lock);
            }
        });

        JLabel jlabelAntennaPower4Lock = new JLabel("Antenna power(0-30dBm)");
        JLabel jlabelKill4Lock = new JLabel("Kill Password");
        JLabel jlabelAccess4Lock = new JLabel("Access Password");
        JLabel jlabelEPC4Lock = new JLabel("EPC memory");
        JLabel jlabelTID4Lock = new JLabel("TID memory");
        JLabel jlabelUSER4Lock = new JLabel("User memory");

        String[] strLockType = { "Unchanged", "Unlock", "Permanent Unlock", "Lock", "Permanent Lock"};
        jcomboboxKill4Lock = new JComboBox(strLockType);
        jcomboboxKill4Lock.setMaximumSize(new Dimension(textfieldWidth,textfieldHeight));
        jcomboboxAccess4Lock = new JComboBox(strLockType);
        jcomboboxAccess4Lock.setMaximumSize(new Dimension(textfieldWidth,textfieldHeight));
        jcomboboxEPC4Lock = new JComboBox(strLockType);
        jcomboboxEPC4Lock.setMaximumSize(new Dimension(textfieldWidth,textfieldHeight));
        jcomboboxTID4Lock = new JComboBox(strLockType);
        jcomboboxTID4Lock.setMaximumSize(new Dimension(textfieldWidth,textfieldHeight));
        jcomboboxUSER4Lock = new JComboBox(strLockType);
        jcomboboxUSER4Lock.setMaximumSize(new Dimension(textfieldWidth,textfieldHeight));

        jcheckboxAccessPwd4Lock = new JCheckBox("Access Password");
        jcheckboxKillPwd4Lock = new JCheckBox("Kill Password");

        jtextfieldAddres4Lock = new JTextField();
        jtextfieldAddres4Lock.setMaximumSize(new Dimension(textfieldEpcWidth,textfieldHeight));
        jtextfield4Lock = new JTextField();
        jtextfield4Lock.setMaximumSize(new Dimension(textfieldWidth,textfieldHeight));

        jtextfieldAccessPwd4Lock = new JTextField();
        jtextfieldAccessPwd4Lock.setMaximumSize(new Dimension(textfieldWidth,textfieldHeight));
        jtextfieldKillPwd4Lock = new JTextField();
        jtextfieldKillPwd4Lock.setMaximumSize(new Dimension(textfieldWidth,textfieldHeight));
        jtextfieldAccessPwd4Lock = new JTextField();
        jtextfieldAccessPwd4Lock.setMaximumSize(new Dimension(textfieldWidth,textfieldHeight));

        JPanel jpanelLock = new JPanel();

        GroupLayout layoutLock = new GroupLayout(jpanelLock);
        layoutLock.setVerticalGroup(layoutLock.createSequentialGroup()
                .addGroup(layoutLock.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(jbuttonAddres4Lock)
                        .addComponent(jtextfieldAddres4Lock)
                        .addComponent(jlabelAntennaPower4Lock)
                        .addComponent(jtextfield4Lock)
                )
                .addGroup(layoutLock.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(jcheckboxAccessPwd4Lock)
                        .addComponent(jtextfieldAccessPwd4Lock)
                        .addComponent(jcheckboxKillPwd4Lock)
                        .addComponent(jtextfieldKillPwd4Lock))
                .addGroup(layoutLock.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(jlabelKill4Lock)
                        .addComponent(jcomboboxKill4Lock))
                .addGroup(layoutLock.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(jlabelAccess4Lock)
                        .addComponent(jcomboboxAccess4Lock))
                .addGroup(layoutLock.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(jlabelEPC4Lock)
                        .addComponent(jcomboboxEPC4Lock))
                .addGroup(layoutLock.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(jlabelTID4Lock)
                        .addComponent(jcomboboxTID4Lock))
                .addGroup(layoutLock.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(jlabelUSER4Lock)
                        .addComponent(jcomboboxUSER4Lock))
        );
        layoutLock.setHorizontalGroup(layoutLock.createSequentialGroup()
                .addGroup(layoutLock.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jlabelAntennaPower4Lock)
                        .addComponent(jcheckboxAccessPwd4Lock)
                        .addComponent(jlabelKill4Lock)
                        .addComponent(jlabelAccess4Lock)
                        .addComponent(jlabelEPC4Lock)
                        .addComponent(jlabelTID4Lock)
                        .addComponent(jlabelUSER4Lock))
                .addGroup(layoutLock.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jtextfield4Lock)
                        .addComponent(jtextfieldAccessPwd4Lock)
                        .addComponent(jcomboboxKill4Lock)
                        .addComponent(jcomboboxAccess4Lock)
                        .addComponent(jcomboboxEPC4Lock)
                        .addComponent(jcomboboxTID4Lock)
                        .addComponent(jcomboboxUSER4Lock))
                .addGroup(layoutLock.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jbuttonAddres4Lock)
                        .addComponent(jcheckboxKillPwd4Lock))
                .addGroup(layoutLock.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jtextfieldAddres4Lock)
                        .addComponent(jtextfieldKillPwd4Lock))
        );
        layoutLock.setAutoCreateGaps(true);
        layoutLock.setAutoCreateContainerGaps(true);

        jpanelLock.setLayout(layoutLock);
        jpanelLock.setVisible(true);
        return jpanelLock;
    }

    boolean lockilling = false;
    void lockillHandler() {
        if (isConnected == false) {
            jtextArea.append("\nNo connection. Cannot lock/kill"); return;
        }
        if (lockilling == false && lockillRunning == false) {
            String lockillingAddress = null;
            int powerLevel = 300;
            try {
                lockillingAddress = jtextfieldAddres4Lock.getText();
                powerLevel = Integer.parseInt(jtextfield4Lock.getText());
            } catch (Exception ex) {
            }
            System.out.println("lockillHandler(): the selected string = " + lockillingAddress + ", powerLevel = " + powerLevel);
            if (lockillingAddress == null)  return;
            if (lockillingAddress.length() == 0)    return;
            if (cs108javalibrary.setSelectedTag(lockillingAddress, powerLevel, false) == false) return;

            boolean startedOperation = false;
            boolean killOperating = false;
            int accessBank = 1, accOffset = 0, accSize = 4; boolean ready = false;
            long password;
            try {
                password = Long.parseLong(jtextfieldAccessPwd4Lock.getText());
            } catch (Exception ex) { return; }

            if (jcheckboxAccessPwd4Lock.isSelected()) {                
                if (cs108javalibrary.mRfidDevice.mRx000Device.mRx000Setting.setRx000AccessPassword(password) == false) return;
                ready = true;
            } else if (jcheckboxKillPwd4Lock.isSelected()) {
                if (cs108javalibrary.mRfidDevice.mRx000Device.mRx000Setting.setRx000KillPassword(password) == false) return;
                ready = true;
            }
/*            if (ready) {
                cs108javalibrary.mRfidDevice.mRx000Device.mRx000Setting.setAccessBank(accessBank);
                cs108javalibrary.mRfidDevice.mRx000Device.mRx000Setting.setAccessOffset(accOffset);
                cs108javalibrary.mRfidDevice.mRx000Device.mRx000Setting.setAccessCount(accSize);
                        if (jcheckboxWrite4ReadWrite.isSelected()) {
                            String strWriteData = "";
                            
                                strWriteData = jtextfieldKillPwd4ReadWrite.getText();
                            
                                strWriteData = jtextfieldAccessPwd4ReadWrite.getText();
                            
                                strWriteData = jtextfieldPC4ReadWrite.getText();
                            
                                strWriteData = jtextfieldEPC4ReadWrite.getText();
                            
                                strWriteData = jtextfieldTIDData4ReadWrite.getText();
                          
                                strWriteData = jtextfieldUSERData4ReadWrite.getText();
                            
                                strWriteData = jtextfieldData4ReadWrite.getText();
                            }
                            jtextArea.append("\nstrWritData = " + strWriteData);
                            cs108javalibrary.mRfidDevice.mRx000Device.mRx000Setting.setAccessWriteData(strWriteData);
                            killOperating = true;
                            cs108javalibrary.mRfidDevice.mRx000Device.sendHostRegRequestHST_CMD(CS108JavaLibrary.HostCommands.CMD_18K6CBLOCKWRITE);
                        } else {
                            killOperating = false;
                            cs108javalibrary.mRfidDevice.mRx000Device.sendHostRegRequestHST_CMD(CS108JavaLibrary.HostCommands.CMD_18K6CREAD);
                        }
                        startedOperation = true;
                    }
                }
*/

            lockilling = true;
            Thread thread1 = new Thread(runnableLockill);
            thread1.start();
            setVisibleConnectOperating();
            jtabbedPane.setSelectedIndex(7); jbuttonLockill.setText(lockillStopText); jbuttonLockill.setVisible(true);
        } else {
            lockilling = false;
            System.out.println("lockillHandler(): lockilling = " + lockilling);
            cs108javalibrary.abortOperation();
        }
    }

    boolean lockillRunning = false;
    Runnable runnableLockill = new Runnable() {
        @Override
        public void run() {
            lockillRunning = true;

            long timeMillis = System.currentTimeMillis();
//            boolean ending = false;
            boolean timeout = false;
            CS108JavaLibrary.Rx000pkgData rx000pkgData;

            cs108javalibrary.appendToLog("runnableLockill(): enter the loop, lastCommandDuration = " + cs108javalibrary.getMacLastCommandDuration(false));
            while (cs108javalibrary.isBleConnected() /*&& ending == false*/ && timeout == false && isConnected && lockilling) {
                rx000pkgData = cs108javalibrary.onRFIDEvent();
                if (cs108javalibrary.mrfidToWriteSize() != 0) {
                    System.out.println("runnableLockill(): mrfidToWriteSize = " + cs108javalibrary.mrfidToWriteSize());
                    try {
                        Thread.sleep(sleepInInventory);
                    } catch (InterruptedException ex) { }
                    timeMillis = System.currentTimeMillis();
                }/* else if (rx000pkgData != null) {
                    if (rx000pkgData.responseType == null) {
                        jtextArea.append("\nrunnableLockill(): null response");
                    } else if (rx000pkgData.responseType == CS108JavaLibrary.HostCmdResponseTypes.TYPE_18K6C_TAG_ACCESS) {
                        if (rx000pkgData.dataValues.length < 12) {
                            jtextArea.append("\nrunnableLockill(): invalid rx000pkgData.bytesData = " + cs108javalibrary.byteArrayToString(rx000pkgData.dataValues));
                        } else {
                            int backscatterError = (rx000pkgData.dataValues[13 - 8] & 0xFF);
                            int macAccessError = (rx000pkgData.dataValues[14 - 8] & 0xFF) + (rx000pkgData.dataValues[15 - 8] & 0xFF) * 256;
                            int writtenLength = (rx000pkgData.dataValues[16 - 8] & 0xFF) + (rx000pkgData.dataValues[17 - 8] & 0xFF) * 256;
                            if (backscatterError == 0 && macAccessError == 0) {
                                byte[] dataRead = new byte[rx000pkgData.dataValues.length - 12];
                                System.arraycopy(rx000pkgData.dataValues, 12, dataRead, 0, dataRead.length);
                                if (killOperating && rx000pkgData.dataValues[12 - 8] == (byte) 0xC7 && writtenLength != 0) {
                                   
                                        jcheckboxKillPwd4ReadWrite.setSelected(false);
                                  
                                        jcheckboxAccessPwd4ReadWrite.setSelected(false);
                       
                                        jcheckboxPC4ReadWrite.setSelected(false);
                          
                                        jcheckboxEPC4ReadWrite.setSelected(false);
                      
                                        jcheckboxTIDData4ReadWrite.setSelected(false);
                         
                                        jcheckboxUSERData4ReadWrite.setSelected(false);
                      
                                        jcheckboxData4ReadWrite.setSelected(false);
                                    }
                                } else if (killOperating == false && rx000pkgData.dataValues[12 - 8] == (byte) 0xC2) {
                                 
                                        jcheckboxKillPwd4ReadWrite.setSelected(false);
                                        jtextfieldKillPwd4ReadWrite.setText(cs108javalibrary.byteArrayToString(dataRead));
                                
                                        jcheckboxAccessPwd4ReadWrite.setSelected(false);
                                        jtextfieldAccessPwd4ReadWrite.setText(cs108javalibrary.byteArrayToString(dataRead));
                            
                                        jcheckboxPC4ReadWrite.setSelected(false);
                                        jtextfieldPC4ReadWrite.setText(cs108javalibrary.byteArrayToString(dataRead));
                                 
                                        jcheckboxEPC4ReadWrite.setSelected(false);
                                        jtextfieldEPC4ReadWrite.setText(cs108javalibrary.byteArrayToString(dataRead));
                                
                                        jcheckboxTIDData4ReadWrite.setSelected(false);
                                        jtextfieldTIDData4ReadWrite.setText(cs108javalibrary.byteArrayToString(dataRead));
                                 
                                        jcheckboxUSERData4ReadWrite.setSelected(false);
                                        jtextfieldUSERData4ReadWrite.setText(cs108javalibrary.byteArrayToString(dataRead));
                            
                                        jcheckboxData4ReadWrite.setSelected(false);
                                        jtextfieldData4ReadWrite.setText(cs108javalibrary.byteArrayToString(dataRead));
                                    }
                                }
                                System.out.println("\nrunnableLockill(): VALID rx000pkgData.bytesData = " + cs108javalibrary.byteArrayToString(rx000pkgData.dataValues));
                            } else {
                                String err = "runnableLockill(): invalid rx000pkgData.bytesData = " + cs108javalibrary.byteArrayToString(rx000pkgData.dataValues)
                                + ", backscatterError = " + backscatterError + ", macAccessError = " + macAccessError;
                                System.out.println(err);
                                jtextArea.append("\n" + err);
                            }
                        }
                    } else if (rx000pkgData.responseType == CS108JavaLibrary.HostCmdResponseTypes.TYPE_COMMAND_END) {
                        System.out.println("runnableLockill(): rx000pkgData.TYPE_COMMAND_END is received");
                        ending = false;
                    }
                    else    System.out.println("runnableLockill(): invalid rx000pkgData.response = " + rx000pkgData.responseType.name() + " is received");
                }*/
                if (System.currentTimeMillis() - timeMillis > 10000) {
                    System.out.println("runnableLockill(): Exit as TIMEOUT");
                    timeout = true;
                }
            }
            System.out.println("runnableLockill(): exit the loop");
            if (cs108javalibrary.isBleConnected() && isConnected) {
                System.out.println("runnableLockill(): Ending LocKill");
                cs108javalibrary.getMacLastCommandDuration(true);
                cs108javalibrary.abortOperation();
                timeMillis = System.currentTimeMillis();
                while (cs108javalibrary.isBleConnected()) {
                    rx000pkgData = cs108javalibrary.onRFIDEvent(); 
                    System.out.println("runnableLockill(): mrfidToWriteSize = " + cs108javalibrary.mrfidToWriteSize() + "rx000pkgData = " + (rx000pkgData == null ? true : false));
                    if (cs108javalibrary.mrfidToWriteSize() == 0 && rx000pkgData == null) {
                        if (System.currentTimeMillis() - timeMillis > 2000) {
                            System.out.println("runnableLockill(): Exit as TIMEOUT");
                            break;
                        }
                    } else timeMillis = System.currentTimeMillis();
                }
                long lastCommandDuration = cs108javalibrary.getMacLastCommandDuration(false);
                cs108javalibrary.appendToLog("runnableLockill(): lastCommandDuration = " + lastCommandDuration);
                if (timeout) {
                    if (lastCommandDuration == 0)
                        System.out.println("runnableLockill(): Exit as RFID RESET");
                    else
                        System.out.println("runnableLockill(): Confirmed Exit as TIMEOUT");
                }
            }
            System.out.println("runnableLockill(): Ending finishes");

            lockillRunning = false;
            jbuttonLockill.setText(lockillStartText);
            setVisibleConnectStandby();           
        }
    };

}
