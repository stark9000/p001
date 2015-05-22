/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ijreport;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.imageio.ImageIO;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

/**
 *

 */
public class reportgen {

    private String Header;
    private String CO_Name;
    private String CO_CompanyName;
    private String CO_StreetAddress;
    private String CO_Phone;
    private String CustomerID;

    private String Date;
    private String Number;
    private String CU_Name;
    private String CU_StreetAddress;
    private String CU_Phone;

    private InputStream payload = null;

    public InputStream getPayload() {
        return payload;
    }

    public void setPayload(InputStream payload) {
        this.payload = payload;
    }

    public void setCO_Name(String CO_Namev) {
        this.CO_Name = CO_Namev;
    }

    public void setHeader(String Headerv) {
        this.Header = Headerv;
    }

    public void setCO_CompanyName(String CO_CompanyNamev) {
        this.CO_CompanyName = CO_CompanyNamev;
    }

    public void setCO_StreetAddress(String CO_StreetAddressv) {
        this.CO_StreetAddress = CO_StreetAddressv;
    }

    public void setCO_Phone(String CO_Phonev) {
        this.CO_Phone = CO_Phonev;
    }

    public void setCustomerID(String CustomerIDv) {
        this.CustomerID = CustomerIDv;
    }

    public void setDate(String Datev) {
        this.Date = Datev;
    }

    public void setNumber(String Numberv) {
        this.Number = Numberv;
    }

    public void setCU_Name(String CU_Namev) {
        this.CU_Name = CU_Namev;
    }

    public void setCU_StreetAddress(String CU_StreetAddressv) {
        this.CU_StreetAddress = CU_StreetAddressv;
    }

    public void setCU_Phone(String CU_Phonev) {
        this.CU_Phone = CU_Phonev;
    }

    public String getHeader() {
        return Header;
    }

    public String getCO_Name() {
        return CO_Name;
    }

    public String getCO_CompanyName() {
        return CO_CompanyName;
    }

    public String getCO_StreetAddress() {
        return CO_StreetAddress;
    }

    public String getCO_Phone() {
        return CO_Phone;
    }

    public String getCustomerID() {
        return CustomerID;
    }

    public String getDate() {
        return Date;
    }

    public String getNumber() {
        return Number;
    }

    public String getCU_Name() {
        return CU_Name;
    }

    public String getCU_StreetAddress() {
        return CU_StreetAddress;
    }

    public String getCU_Phone() {
        return CU_Phone;
    }

    public static InputStream imageTostream(String Path) {
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(Path));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        return is;
    }

    public static byte[] extractBytes(String Path) throws IOException {
        File imgPath = new File(Path);
        BufferedImage bufferedImage = ImageIO.read(imgPath);
        // get DataBufferBytes from Raster
        WritableRaster raster = bufferedImage.getRaster();
        DataBufferByte data = (DataBufferByte) raster.getDataBuffer();

        return (data.getData());
    }
//****************************************************//

    public static Collection getDataList() {
        Vector repgv = new Vector();
        try {
            reportgen repg = new reportgen();
            repg.setPayload(imageTostream("./images.jpg"));
            repg.setHeader("INVOICE");
            repg.setCU_Name("0007");
            repg.setCU_StreetAddress("noWa efsf sfsdfsdf fdsgdg gfdgdfgdfgfg");
            repg.setCU_Phone("000-00000");
            repg.setCustomerID("112323434");

            repg.setCO_Name("coown-name");
            repg.setCO_CompanyName("co-name");
            repg.setCO_StreetAddress("kandy");
            repg.setCO_Phone("000-4345345");
            repgv.add(repg);
            repg = new reportgen();

        } catch (Exception ex) {
            System.out.println(ex);
        }
        return repgv;
    }

    public static void main(String[] str) {
        try {
            JasperReport jasperReport = null;
            JasperPrint jasperPrint = null;
            JasperDesign jasperDesign = null;
            Map parameters = new HashMap();
            jasperDesign = JRXmlLoader.load("./Untitled_report_1.jrxml");
            jasperReport = JasperCompileManager.compileReport(jasperDesign);
            jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JRBeanCollectionDataSource(ijreport.reportgen.getDataList()));
//            JasperExportManager.exportReportToPdfFile(jasperPrint, "StudentInfo.pdf");
            JasperViewer.viewReport(jasperPrint);
        } catch (Exception ex) {
            System.out.println("EXCEPTION: " + ex);
        }
    }
}
