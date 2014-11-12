package Util;

import com.itextpdf.text.*;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfOutline;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.stage.Stage;
import persistance.*;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * This class generates a PDF out of Bill-Objects
 * Created by olfad on 25.02.14.
 */
public class Writer {

    private static final String BILL_FILE_PREFIX = "Tulla Garten Rechnung ";
    private static final Phrase TABLE_DELIMITER = new Phrase("_______________________________________________________");
    private static final float COMPANY_ADDRESS_BLOCK_SPACE_AFTER = 15;
    private static final float CUSTOMER_ADDRESS_BLOCK_SPACE_AFTER = 15;
    private static final float DATE_SPACE_AFTER = 10;
    private static final float COMPANY_ADDRESS_BLOCK_SPACE_BEFORE = 45;

    public static void writeBill(Bill bill) throws DocumentException, FileNotFoundException, NoSuchDBEntryException, SQLException {
        if(bill == null){
            return;
        }

        Document document = new Document();


        Customer customer = bill.getCustomer();

        String pfdFileName = BILL_FILE_PREFIX + "#" + bill.getNumber() + " " + customer.getLastname()+".pdf";

        PdfWriter.getInstance(document, new FileOutputStream(pfdFileName));

        document.open();

        writeCompanyAddressblock(document);

        writeDate(bill, document);

        writeCustomerAddressBlock(document, customer);

        writeBillDescription(bill, document);

        writeContentsTable(bill, document);

        writeText(bill, document);

        document.close();

        if (Desktop.isDesktopSupported()) {
            try {
                File myFile = new File(pfdFileName);
                Desktop.getDesktop().open(myFile);
            } catch (IOException ex) {
                // no application registered for PDFs
            }
        }


    }

    private static void writeText(Bill bill, Document document) throws DocumentException {
        Paragraph paymentText = new Paragraph();
        Phrase text = new Phrase(bill.getText());

        paymentText.add(text);

        document.add(paymentText);
    }

    private static void writeContentsTable(Bill bill, Document document) throws DocumentException, NoSuchDBEntryException, SQLException {

        float balance = 0;

        PdfPTable contents = new PdfPTable(4);
        contents.setWidthPercentage(100);
        PdfPCell tableDelimiter = new PdfPCell(TABLE_DELIMITER);
        tableDelimiter.setColspan(4);


        contents.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        contents.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);


        SQLiteDBManager dbManager = new SQLiteDBManager();

        for(Project p : dbManager.getProjectsByBill(bill)){

            PdfPCell descCell = new PdfPCell(new Phrase(p.getName()));
            descCell.setColspan(4);
            descCell.setBorder(Rectangle.BOTTOM);
            contents.addCell(descCell);


            PdfPCell cell = new PdfPCell(new Phrase("  "+p.getDescription()));
            cell.setColspan(4);
            cell.setBorder(Rectangle.NO_BORDER);

            contents.addCell(cell);

            for(Position pos : dbManager.getPositionByProject(p)){

                contents.addCell("  "+pos.getProduct().getName());
                contents.addCell(pos.getAmount()+" " + pos.getProduct().getUnit());

                contents.addCell(pos.getPricePerUnit() + " €");
                contents.addCell(pos.getPrice() +" €");

                balance += pos.getPrice();
            }



            PdfPCell spacer = new PdfPCell(new Phrase("  "));
            spacer.setColspan(4);
            spacer.setBorder(Rectangle.NO_BORDER);
            spacer.setFixedHeight(10);

            contents.addCell(spacer);

        }
        document.add(contents);


        Paragraph totalPriceParagraph = new Paragraph();
        totalPriceParagraph.setAlignment(Element.ALIGN_RIGHT);

        Phrase totalPirce = new Phrase("Preis excl. 20% Ust.: \t"+balance+" €\n");
        Phrase totalTax = new Phrase("+20% Ust.: \t"+(balance*0.2)+" €\n");
        Phrase fianlPrice = new Phrase("Gesammtpreis: \t"+(balance*1.2)+" €\n");
        totalPriceParagraph.addAll(Arrays.asList(totalPirce,totalTax,fianlPrice));
        document.add(totalPriceParagraph);

    }

    private static void writeBillDescription(Bill bill, Document document) throws DocumentException {
        Paragraph billingDescription = new Paragraph();
        billingDescription.setSpacingAfter(Utilities.millimetersToPoints(2));
        Phrase billingDesc = new Phrase(bill.getType() + " " + bill.getNumber());
        billingDescription.add(billingDesc);
        document.add(billingDescription);
    }

    private static void writeCompanyAddressblock(Document document) throws DocumentException {
        Paragraph companyAddressBlock = new Paragraph();
        companyAddressBlock.setSpacingAfter(Utilities.millimetersToPoints(COMPANY_ADDRESS_BLOCK_SPACE_BEFORE));
        companyAddressBlock.setSpacingAfter(Utilities.millimetersToPoints(COMPANY_ADDRESS_BLOCK_SPACE_AFTER));
        Phrase companyName = new Phrase (Properties.get("companyName")+"\n");
        Phrase companyAddress = new Phrase (Properties.get("companyAddress")+"\n");
        Phrase companyAddress2 = new Phrase ("");
        Phrase companyCity = new Phrase (Properties.get("companyCity")+"\n");
        companyAddressBlock.addAll(Arrays.asList(companyName, companyAddress, companyAddress2, companyCity));
        document.add(companyAddressBlock);
    }

    private static void writeDate(Bill bill, Document document) throws DocumentException {
        Paragraph billingDate = new Paragraph(Element.ALIGN_RIGHT);
        billingDate.setAlignment(Element.ALIGN_RIGHT);
        billingDate.setSpacingAfter(Utilities.millimetersToPoints(DATE_SPACE_AFTER));
        Phrase date = new Phrase(bill.getDate().toString());
        billingDate.add(date);
        document.add(billingDate);
    }

    private static void writeCustomerAddressBlock(Document document, Customer customer) throws DocumentException {
        //Customer
        Paragraph customerAddressBlock = new Paragraph();
        customerAddressBlock.setSpacingAfter(Utilities.millimetersToPoints(CUSTOMER_ADDRESS_BLOCK_SPACE_AFTER));
        Phrase customerName;
        Phrase customerCompany;
        Phrase customerAddress2;

        if(customer.getSalutation() != null && !customer.getSalutation().isEmpty()){
            customerName = new Phrase(customer.getSalutation()+"\n");
            customerAddressBlock.add(customerName);
        }
        if(customer.getCompany() != null && !customer.getCompany().isEmpty()){
            customerCompany = new Phrase(customer.getCompany()+"\n");
            customerAddressBlock.add(customerCompany);
        }

        Phrase customerAdress = new Phrase(customer.getAddressLine()+"\n");
        customerAddressBlock.add(customerAdress);
        if(customer.getAddressLine2() != null && !customer.getAddressLine2().isEmpty()) {
            customerAddress2 = new Phrase(customer.getAddressLine2() + "\n");
            customerAddressBlock.add(customerAddress2);
        }
        Phrase customerCity = new Phrase(customer.getZip() + " " + customer.getCity());
        customerAddressBlock.add(customerCity);
        document.add(customerAddressBlock);
    }
}
