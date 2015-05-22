/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package p001;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.view.JasperViewer;
import simplemysql.SimpleMySQL;
import simplemysql.SimpleMySQLResult;
import xu.dateandtime;
import xu.sessioncookie;

/**
 *
 * @author xBerWise
 */
public final class NewMFrame extends javax.swing.JFrame {

    private int qty = 0;
    private double unitprice = 0;
    private double amount = 0;
    private DecimalFormat df = new DecimalFormat("#.##");
    private sessioncookie SC;
    private SimpleMySQL MYSQL;
    private long CustomerID;
//    private long JobID;
    private long ReportID;
    private long ItemID;
    private dateandtime DATEO;
    private String amountA = "";
    private DefaultTableModel dfm;
    private String Phone2 = "000-0000000";
    private String empID;
    private long nonpkk;

    /**
     * Creates new form NewJFrame1
     */
    public NewMFrame() {
        initComponents();
        InitSetup();
    }

    public void InitSetup() {
        MYSQL = SimpleMySQL.getInstance();
        SC = sessioncookie.getInstance();
        DATEO = new dateandtime();
        if (SC.getSessionCookie().size() == 3) {
            jLabel15.setText(" Welcome " + SC.getSessionCookie().get(0).toString());
            jLabel17.setText(DATEO.getDate());
            setIDS();
            dfm = (DefaultTableModel) jTable1.getModel();
            dfm.addTableModelListener(new TableModelListener() {

                @Override
                public void tableChanged(TableModelEvent e) {

                    getAmountT();
                }
            });

        } else {
            SC.DestroySession();
            new login().setVisible(true);
            this.dispose();
        }

    }

    public void getAmountT() {
        double Total = 0;
        for (int i = 0; i < dfm.getRowCount(); i++) {
            Total = Total + Double.parseDouble(dfm.getValueAt(i, 4).toString());
        }
        amountA = String.format("%.2f", Total);
        jLabel20.setText(amountA);
    }

    public void setIDS() {
        setCustomerID();
//        setJobID();
        setReportID();
        setItemID();
        setEmpID();
        jTextField2.setText(String.valueOf(CustomerID));
        jTextField3.setText(String.valueOf(ReportID));
//        jTextField6.setText(String.valueOf(JobID));
    }

    public void getAmount() {
        if (jFormattedTextField3.getText().isEmpty() || jFormattedTextField4.getText().isEmpty()) {

        } else {
            qty = Integer.parseInt(jFormattedTextField3.getText());
            unitprice = Double.parseDouble(jFormattedTextField4.getText());
            amount = unitprice * qty;
            jTextField5.setText(String.format("%.2f", amount));
        }
    }

    public void PrintAndSave() {
        try {
            if (jTextField1.getText().isEmpty()) {
                return;
            } else if (jFormattedTextField1.getText().isEmpty()) {
                return;
            } else if (jCheckBox1.isSelected()) {
                if (jFormattedTextField2.getText().isEmpty()) {
                    return;
                }
            } else if (jTextField4.getText().isEmpty()) {
                return;

            } else if (jTextArea1.getText().isEmpty()) {
                return;

            } else if (jFormattedTextField3.getText().isEmpty()) {
                return;

            } else if (jFormattedTextField4.getText().isEmpty()) {
                return;
            } else {
                setIDS();

                DefaultTableModel de = (DefaultTableModel) jTable1.getModel();

                if (de.getRowCount() == 0) {
                    return;
                } else if (CustomerID == -1 || ReportID == -1) {
                    return;
                }
                if (jFormattedTextField2.getText().isEmpty()) {
                    Phone2 = "000-0000000";
                }
//                for (int i = 0; i < de.getRowCount(); i++) {
//                    System.out.println(de.getValueAt(i, 5));
//                    System.out.println(de.getValueAt(i, 0));
//                    System.out.println(de.getValueAt(i, 1));
//                    System.out.println(de.getValueAt(i, 2));
//                    System.out.println(de.getValueAt(i, 3));
//                    System.out.println(de.getValueAt(i, 4));
//                }
                MYSQL.Query("INSERT INTO customer(idcustomer,Name,Phone1,Phone2) VALUES ('" + CustomerID + "',  '" + jTextField1.getText() + "','" + jFormattedTextField1.getText() + "','" + Phone2 + "')");
                for (int i = 0; i < de.getRowCount(); i++) {
                    setItemID();
                    MYSQL.Query("INSERT INTO items(iditems,itemsidnpk,Code,Itemdesc,Qty,UnitPrice,Amount,customer_idcustomer,ReportID) VALUES ('" + ItemID + "','" + Integer.parseInt(de.getValueAt(i, 5).toString()) + "','" + de.getValueAt(i, 0) + "','" + de.getValueAt(i, 1) + "','" + de.getValueAt(i, 2) + "','" + de.getValueAt(i, 3) + "','" + de.getValueAt(i, 4) + "','" + CustomerID + "','" + ReportID + "')");

                }
                MYSQL.Query("INSERT INTO report (idreport,Reterened,employee_idemployee,customer_idcustomer,reportdate,paymentM,type) VALUES ('" + ReportID + "','" + "N_A" + "','" + empID + "','" + CustomerID + "','" + DATEO.getDate() + "','" + jComboBox2.getSelectedItem().toString() + "','" + jComboBox1.getSelectedItem().toString() + "')");
                try {

                    JRTableModelDataSource datasource = new JRTableModelDataSource(de);
                    String reportSource = "./report.jrxml";

                    JasperReport jr = JasperCompileManager.compileReport(reportSource);

                    Map<String, Object> params = new HashMap<>();
                    params.put("invt", "INVOICE");
                    params.put("cname", jTextField1.getText());
                    params.put("invno", String.valueOf(ReportID));
                    params.put("datel", DATEO.getDate());
                    params.put("terms", jComboBox2.getSelectedItem().toString());
                    params.put("repcode", SC.getSessionCookie().get(0).toString());
                    params.put("subtotal", amountA);
                    params.put("discount", "00.00");
                    params.put("nettotal", amountA);

                    JasperPrint jp = JasperFillManager.fillReport(jr, params, datasource);

                    JasperViewer.viewReport(jp, false);
                } catch (JRException e) {
                    // e.printStackTrace();
                }
            }
            clearA();
            setIDS();
        } catch (Exception e) {

        }

    }

    public void clearA() {
        jTextField1.setText("");
        jFormattedTextField1.setText("");
        Phone2 = "000-0000000";
        jTextField4.setText("");
        jTextArea1.setText("");
        jFormattedTextField3.setText("");
        jFormattedTextField4.setText("");
        jTextField5.setText("0.0");
        DefaultTableModel dfm = (DefaultTableModel) jTable1.getModel();
        dfm.getDataVector().removeAllElements();
        dfm.fireTableDataChanged();

//        DefaultTableModel dm = (DefaultTableModel) table.getModel();
//        while (dm.getRowCount() > 0) {
//            dm.removeRow(0);
//        }
    }

    public void setCustomerID() {
        SimpleMySQLResult result;
        result = MYSQL.Query("SELECT MAX(idcustomer) FROM customer");
        if (result.getString(1) == null) {
            this.CustomerID = -1;
        } else {
            this.CustomerID = Long.parseLong(result.getString(1)) + 1;
        }

    }

//    public void setJobID() {
//        SimpleMySQLResult result;
//        result = MYSQL.Query("SELECT MAX(idjobs) FROM jobs");
//        if (result.getString(1) == null) {
//            this.JobID = -1;
//        } else {
//            this.JobID = Long.parseLong(result.getString(1)) + 1;
//        }
//
//    }
    public void setReportID() {
        SimpleMySQLResult result;
        result = MYSQL.Query("SELECT MAX(idreport) FROM report");
        if (result.getString(1) == null) {
            this.ReportID = -1;
        } else {
            this.ReportID = Long.parseLong(result.getString(1)) + 1;
        }

    }

    public void setItemID() {
        SimpleMySQLResult result;
        result = MYSQL.Query("SELECT MAX(iditems) FROM items");
        if (result.getString(1) == null) {
            this.ItemID = -1;
        } else {
            this.ItemID = Long.parseLong(result.getString(1)) + 1;
        }

    }

    public void setEmpID() {
        SimpleMySQLResult result;
        result = MYSQL.Query("SELECT idemployee FROM employee WHERE Name = '" + SC.getSessionCookie().get(0).toString() + "'");
        if (result.getString(1) == null) {
            this.empID = "";
        } else {
            this.empID = result.getString(1).toString();
        }

    }

    public void setNonpkk() {
        SimpleMySQLResult result;
        result = MYSQL.Query("SELECT MAX(itemsidnpk) FROM items");
        if (result.getString(1) == null) {
            this.nonpkk = -1;
        } else {
            this.nonpkk = Long.parseLong(result.getString(1)) + 1;
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jFormattedTextField1 = new javax.swing.JFormattedTextField();
        jFormattedTextField2 = new javax.swing.JFormattedTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jCheckBox1 = new javax.swing.JCheckBox();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel11 = new javax.swing.JLabel();
        jFormattedTextField3 = new javax.swing.JFormattedTextField();
        jLabel12 = new javax.swing.JLabel();
        jFormattedTextField4 = new javax.swing.JFormattedTextField();
        jLabel13 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jButton4 = new javax.swing.JButton();
        jToggleButton1 = new javax.swing.JToggleButton();
        jLabel18 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("Customer Information :");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 11, -1, -1));

        jLabel2.setText("Name             :");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 39, -1, -1));

        jLabel3.setText("Phone No -1  :");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 65, -1, -1));

        jLabel4.setText("Phone No -2  :");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 91, -1, -1));
        getContentPane().add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 36, 128, -1));
        getContentPane().add(jFormattedTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 62, 128, -1));

        jFormattedTextField2.setText("000-0000000");
        jFormattedTextField2.setEnabled(false);
        getContentPane().add(jFormattedTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(89, 88, 129, -1));

        jPanel1.setBackground(new java.awt.Color(204, 204, 255));

        jLabel8.setText("ID :");

        jTextField2.setEditable(false);
        jTextField2.setBackground(new java.awt.Color(255, 255, 255));

        jCheckBox1.setBackground(new java.awt.Color(204, 204, 255));
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jLabel15.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel16.setText("Login infomarion :");
        jLabel16.setBorder(javax.swing.BorderFactory.createCompoundBorder());

        jLabel17.setText("date");

        jButton1.setText("Logout");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton5.setText("back");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(226, 226, 226)
                        .addComponent(jCheckBox1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 91, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel8)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel16)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(8, 8, 8))
                            .addComponent(jTextField2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(51, 51, 51)
                                .addComponent(jCheckBox1, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jButton1)
                                    .addComponent(jButton5))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 740, 120));

        jPanel2.setBackground(java.awt.SystemColor.inactiveCaptionText);

        jLabel5.setText("Bill Information :");

        jLabel6.setText("Type         :");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Service", "Other" }));

        jLabel7.setText("Invoice No :");

        jTextField3.setEditable(false);
        jTextField3.setBackground(new java.awt.Color(255, 255, 255));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Code", "Itm Desc", "Qty", "Unt Price", "Amount", "ItemID"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setFillsViewportHeight(true);
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(5).setResizable(false);
        }

        jLabel9.setText("Code         :");

        jLabel10.setText("Item Desc :");

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane2.setViewportView(jTextArea1);

        jLabel11.setText("Qty          :");

        jFormattedTextField3.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        jFormattedTextField3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jFormattedTextField3KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jFormattedTextField3KeyReleased(evt);
            }
        });

        jLabel12.setText("Unit Price :");

        jFormattedTextField4.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        jFormattedTextField4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jFormattedTextField4KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jFormattedTextField4KeyReleased(evt);
            }
        });

        jLabel13.setText("Amout      :");

        jTextField5.setEditable(false);
        jTextField5.setBackground(new java.awt.Color(255, 255, 255));
        jTextField5.setText("0.00");

        jButton2.setText("remove");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Print");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Add");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jToggleButton1.setText("edit");
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        jLabel18.setText("payment :");

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "cash", "credit", "cheque" }));

        jLabel19.setText("SubToatal :");

        jLabel20.setText("s");

        jLabel21.setText("NetTotal  :");

        jLabel22.setText("s");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel6)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel18)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(167, 167, 167)
                                        .addComponent(jLabel7)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel13)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jTextField5))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel12)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jFormattedTextField4))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel11)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jFormattedTextField3))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel9)
                                            .addComponent(jLabel10))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                                            .addComponent(jTextField4))))
                                .addGap(10, 10, 10)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 462, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jButton4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jToggleButton1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jLabel19)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGap(4, 4, 4)
                                                .addComponent(jLabel21)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jButton3))))))
                    .addComponent(jSeparator1))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jComboBox1)
                        .addComponent(jLabel18)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(jFormattedTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(jFormattedTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton2)
                            .addComponent(jButton3)
                            .addComponent(jButton4)
                            .addComponent(jToggleButton1)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel19)
                            .addComponent(jLabel20))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel21)
                            .addComponent(jLabel22))))
                .addGap(6, 6, 6)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(55, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 120, 740, 380));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        PrintAndSave();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed

        if (jTable1.getSelectedRow() != -1) {
            if (jToggleButton1.isSelected()) {
                jToggleButton1.setText("done");
                jTextField4.setText(jTable1.getValueAt(jTable1.getSelectedRow(), 0).toString());
                jTextArea1.setText(jTable1.getValueAt(jTable1.getSelectedRow(), 1).toString());
                jFormattedTextField3.setText(jTable1.getValueAt(jTable1.getSelectedRow(), 2).toString());
                jFormattedTextField4.setText(jTable1.getValueAt(jTable1.getSelectedRow(), 3).toString());
                jTextField5.setText(jTable1.getValueAt(jTable1.getSelectedRow(), 4).toString());
            } else {
                jToggleButton1.setText("edit");
                jTextField4.setText("");
                jTextArea1.setText("");
                jFormattedTextField3.setText("");
                jFormattedTextField4.setText("");
                jTextField5.setText("");
            }

        }
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed

        if (jTextField4.getText().isEmpty()) {
            return;
        } else if (jTextArea1.getText().isEmpty()) {
            return;
        } else if (jFormattedTextField3.getText().isEmpty()) {
            return;
        } else if (jFormattedTextField4.getText().isEmpty()) {
            return;
        }

        DefaultTableModel dfm = (DefaultTableModel) jTable1.getModel();

        Vector tbm = new Vector();
        setNonpkk();
        if (nonpkk == -1) {
            return;
        }
        tbm.add(jTextField4.getText());
        tbm.add(jTextArea1.getText());
        tbm.add(jFormattedTextField3.getText());
        tbm.add(jFormattedTextField4.getText());
        tbm.add(jTextField5.getText());
        tbm.add(String.valueOf(nonpkk));
        dfm.addRow(tbm);
        scrollToNewRow(jTable1, jTable1.getRowCount(), 1);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jFormattedTextField3KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFormattedTextField3KeyReleased
        getAmount();
    }//GEN-LAST:event_jFormattedTextField3KeyReleased

    private void jFormattedTextField4KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFormattedTextField4KeyPressed

    }//GEN-LAST:event_jFormattedTextField4KeyPressed

    private void jFormattedTextField3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFormattedTextField3KeyPressed

    }//GEN-LAST:event_jFormattedTextField3KeyPressed

    private void jFormattedTextField4KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFormattedTextField4KeyReleased
        getAmount();
    }//GEN-LAST:event_jFormattedTextField4KeyReleased

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        if (jCheckBox1.isSelected()) {
            jFormattedTextField2.setEnabled(true);
        } else {
            jFormattedTextField2.setText("000-0000000");
            jFormattedTextField2.setEnabled(false);
        }
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        DefaultTableModel dfm = (DefaultTableModel) jTable1.getModel();
        if (dfm.getRowCount() == 0) {
            return;
        }
        int[] rows = jTable1.getSelectedRows();
        for (int i = 0; i < rows.length; i++) {
            dfm.removeRow(rows[i] - i);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        SC.DestroySession();
        new login().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        this.dispose();
        new MainFrame().setVisible(true);
    }//GEN-LAST:event_jButton5ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(NewMFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NewMFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NewMFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NewMFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new NewMFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JFormattedTextField jFormattedTextField1;
    private javax.swing.JFormattedTextField jFormattedTextField2;
    private javax.swing.JFormattedTextField jFormattedTextField3;
    private javax.swing.JFormattedTextField jFormattedTextField4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JToggleButton jToggleButton1;
    // End of variables declaration//GEN-END:variables

    private static void scrollToNewRow(JTable table, int row, int col) {
        JScrollPane sp = getScrollPane(table);
        if (sp == null) {
            return;
        }
        JViewport viewport = sp.getViewport();
        Rectangle r = table.getCellRect(row, col, true);
        Point p = viewport.getViewPosition();
        r.setLocation(r.x - p.x, r.y - p.y + table.getRowHeight());
        viewport.scrollRectToVisible(r);
    }

    private static JScrollPane getScrollPane(Component c) {
        while ((c = c.getParent()) != null) {
            if (c instanceof JScrollPane) {
                return (JScrollPane) c;
            }
        }
        return null;
    }

}
