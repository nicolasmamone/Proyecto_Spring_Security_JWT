package com.api.gestion.service.impl;

import com.api.gestion.constantes.FacturaConstantes;
import com.api.gestion.dao.FacturaDAO;
import com.api.gestion.pojo.Factura;
import com.api.gestion.security.jwt.JwtFilter;
import com.api.gestion.service.FacturaService;
import com.api.gestion.utils.FacturaUtils;
import com.google.common.io.FileBackedOutputStream;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.IOUtils;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
@Service
public class FacturaServiceImpl implements FacturaService {

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private FacturaDAO facturaDAO;

    @Override
    public ResponseEntity<String> generatedReport(Map<String, Object> requestMap) {
        log.info("Dentro del metodo generar reporte");
        try {
            String filename;
            if (validateRequestMap(requestMap)){
                if (requestMap.containsKey("isGenerate") && !(Boolean) requestMap.get("isGenerate")){//si contiene 'isgenerate' y es true
                    filename = (String) requestMap.get("uuid");
                }else{
                    filename = FacturaUtils.getUUId();
                    requestMap.put("uuid", filename);
                    insertarFactura(requestMap);
                }
                String data = "NOMBRE: " + requestMap.get("nombre")
                        + "\nNUMERO DE CONTACTO: " + requestMap.get("numeroContacto")
                        + "\nEMAIL: " + requestMap.get("email")
                        + "\nMETODO DE PAGO: " + requestMap.get("metodoPago");

                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(FacturaConstantes.STORE_LOCATION+"\\" + filename + ".pdf"));

                document.open();
                setRectangleInPdf(document);// Le seteamos los marcos

                //Seteamos el titulo, su tipo de letra y align
                Paragraph paragrapHeader = new Paragraph("GESTION DE CATEGORIAS Y PRODUCTOS", getFont("Header"));
                paragrapHeader.setAlignment(Element.ALIGN_CENTER);
                document.add(paragrapHeader);

                PdfPTable pdfPTable = new PdfPTable(5);
                pdfPTable.setWidthPercentage(100);
                addTableHeader(pdfPTable); //Generamos la cabecera de la tabla

                JSONArray jsonArray = FacturaUtils.getJsonArrayFromString((String) requestMap.get("productoDetalles"));
                for (int i = 0; i < jsonArray.length(); i++) {
                    addRows(pdfPTable, FacturaUtils.getMapFromJson(jsonArray.getString(i)));//generamos cada fila de cada posicion en el jsonArray
                }
                document.add(pdfPTable);

                Paragraph footer = new Paragraph("TOTAL: " + requestMap.get("montoTotal")
                        + "\n" + "Gracias por visitarnos, vuelva pronto !!", getFont("data"));
                document.add(footer);

                document.close();

                return new ResponseEntity<>("{\"uuid\":\""+ filename +"\"}", HttpStatus.OK);
            }

            return FacturaUtils.getResponseEntity("Datos requeridos no encontrados", HttpStatus.BAD_REQUEST);
        }catch(Exception exception){
            exception.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<Factura>> getFacturas() {
        List<Factura> facturas = new ArrayList<>();
        if (jwtFilter.isAdmin()){
            facturas = facturaDAO.getFacturas();
        }else{
            facturas = facturaDAO.getFacturaByUsername(jwtFilter.getCurrentUser());
        }
        return new ResponseEntity<>(facturas, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) {
        log.info("Dentro de getPdf: requestMap{} ",requestMap);
        try {
            byte[] bytesArray = new byte[0];
            if (!requestMap.containsKey("uuid") && validateRequestMap(requestMap)){
                return new ResponseEntity<>(bytesArray, HttpStatus.BAD_REQUEST);
            }
            String filepath = FacturaConstantes.STORE_LOCATION + "\\" + (String) requestMap.get("uuid") + ".pdf";
            if (FacturaUtils.isFileExist(filepath)){ //Si el pdf existe --> mando el byteArray en la respuesta
                bytesArray = getByteArray(filepath);
                return new ResponseEntity<>(bytesArray, HttpStatus.OK);
            }else{ // Sino --> Creamos reporte y luego lo obtenemos
                requestMap.put("isGenerate", false);
                generatedReport(requestMap);
                bytesArray = getByteArray(filepath);
            }
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return null;
    }

    private byte[] getByteArray(String filePath) throws IOException {
        File initialFile = new File(filePath);
        InputStream inputStream = new FileInputStream(initialFile);
        byte[] byteArray = IOUtils.toByteArray(inputStream);
        inputStream.close();
        return byteArray;
    }

    private void setRectangleInPdf(Document document) throws DocumentException {
        log.info("Dentro de setRectangleInPdf");
        Rectangle rectangle = new Rectangle(577,825,18,15);
        rectangle.enableBorderSide(1);
        rectangle.enableBorderSide(2);
        rectangle.enableBorderSide(4);
        rectangle.enableBorderSide(8);
        rectangle.setBorderColor(BaseColor.BLACK);
        rectangle.setBorderWidth(1);
        document.add(rectangle);
    }

    private Font getFont(String type){
        log.info("Dentro de getFont");
        switch (type){
            case "Header": // indicando el tipo de letra del header
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 18, BaseColor.BLACK);
                headerFont.setStyle(Font.BOLD);
                return headerFont;
            case "Data":
                Font dataFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, BaseColor.BLACK);
                dataFont.setStyle(Font.BOLD);
                return dataFont;
            default:
                return new Font();

        }
    }

    //Agregar filas a la tabla
    private void addRows(PdfPTable pdfPTable, Map<String, Object> data){
        log.info("Dentro de addRows");
        pdfPTable.addCell((String) data.get("nombre"));
        pdfPTable.addCell((String) data.get("categoria"));
        pdfPTable.addCell((String) data.get("cantidad"));
        pdfPTable.addCell(Double.toString((Double) data.get("precio")));
        pdfPTable.addCell(Double.toString((Double) data.get("total")));
    }

    //Cabecera de la tabla del pdf
    private void addTableHeader(PdfPTable pdfPTable){
        log.info("Dentro del addTableHeader");
        Stream.of("Nombre", "Categoria", "Cantidad", "Precio", "Subtotal")
                .forEach(columnTitle -> {
                    PdfPCell pdfPCell = new PdfPCell();
                    pdfPCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    pdfPCell.setBorderWidth(2);
                    pdfPCell.setPhrase(new Phrase(columnTitle));
                    pdfPCell.setBackgroundColor(BaseColor.YELLOW);
                    pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    pdfPTable.addCell(pdfPCell);
                });
    }

    //Metodo para poder guardar una factura
    private void insertarFactura(Map<String, Object> requestMap){
        try {
            Factura factura = new Factura();
            factura.setUuid((String) requestMap.get("uuid"));
            factura.setNombre((String) requestMap.get("nombre"));
            factura.setEmail((String) requestMap.get("email"));
            factura.setNumeroContacto((String) requestMap.get("numeroContacto"));
            factura.setMetodoPago((String) requestMap.get("metodoPago"));
            factura.setTotal(Integer.parseInt((String) requestMap.get("montoTotal")));
            factura.setProductoDetalles((String) requestMap.get("productoDetalles"));
            factura.setCreatedBy(jwtFilter.getCurrentUser());
            facturaDAO.save(factura);

        }catch (Exception exception){
            exception.printStackTrace();
        }
    }


    //Metodo para validar el requestMap q le paso a insertarFactura
    private boolean validateRequestMap(Map<String,Object> requestMap){
        return requestMap.containsKey("nombre") &&
                requestMap.containsKey("numeroContacto") &&
                requestMap.containsKey("email") &&
                requestMap.containsKey("metodoPago") &&
                requestMap.containsKey("productoDetalles") &&
                requestMap.containsKey("montoTotal");
    }
}
