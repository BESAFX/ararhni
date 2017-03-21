package com.besafx.app.controller;

import com.besafx.app.config.CustomException;
import com.besafx.app.entity.Person;
import com.besafx.app.entity.Task;
import com.besafx.app.entity.TaskOperation;
import com.besafx.app.search.TaskSearch;
import com.besafx.app.service.TaskService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.*;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.oasis.JROdsExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRPptxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.type.*;
import net.sf.jasperreports.export.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ReportDynamicController {

    @Autowired
    private TaskSearch taskSearch;

    @Autowired
    private TaskService taskService;

    private JasperDesign jasperDesign = new JasperDesign();

    private JRDesignStyle style1 = new JRDesignStyle();

    private JRDesignStyle style2 = new JRDesignStyle();

    private JRDesignBand band = new JRDesignBand();

    private JRDesignStaticText staticText = new JRDesignStaticText();

    private JRDesignImage image = new JRDesignImage(jasperDesign);

    private JRDesignFrame frame = new JRDesignFrame();

    private JRDesignField field = new JRDesignField();

    private JRDesignTextField textField = new JRDesignTextField();

    private JasperDesign getJasperDesign(String reportTitle, String orientation, List<Column> columns, List<Variable> groupVariablesList, List<Variable> tableVariablesList) throws JRException {

        jasperDesign = new JasperDesign();
        jasperDesign.setName("NoXmlDesignReport");
        jasperDesign.setOrientation(OrientationEnum.getByName(orientation));
        jasperDesign.setPageWidth(orientation.equals("Portrait") ? 595 : 842);
        jasperDesign.setPageHeight(orientation.equals("Portrait") ? 842 : 595);
        jasperDesign.setColumnSpacing(0);
        jasperDesign.setLeftMargin(20);
        jasperDesign.setRightMargin(20);
        jasperDesign.setTopMargin(20);
        jasperDesign.setBottomMargin(20);
        jasperDesign.setColumnWidth(jasperDesign.getPageWidth() - jasperDesign.getLeftMargin() - jasperDesign.getRightMargin());
        jasperDesign.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);

        createStyle();

        createPageHeader(reportTitle);

        createBackground();

        createPageFooter();

        createTable(columns, groupVariablesList, tableVariablesList);

        return jasperDesign;
    }

    private void createTable(List<Column> list, List<Variable> groupVariablesList, List<Variable> tableVariablesList) throws JRException {
        //Column Header
        JRDesignBand columnHeader = new JRDesignBand();
        columnHeader.setHeight(15);

        //Detail
        JRDesignBand detail = new JRDesignBand();
        detail.setHeight(15);

        Iterator<Column> iterator = list.iterator();

        List<Column> activeList = list.stream().filter(col -> col.isView()).collect(Collectors.toList());

        while (iterator.hasNext()) {

            Column column = iterator.next();

            if (!jasperDesign.getFieldsList().stream().filter(field -> field.getName().equals(column.getValue())).findAny().isPresent()) {
                field = new JRDesignField();
                field.setName(column.getValue());
                field.setValueClassName(column.getValueClassName());
                jasperDesign.addField(field);
            }

            if (column.isView()) {

                staticText = new JRDesignStaticText();
                staticText.setWidth(jasperDesign.getColumnWidth() / activeList.size());
                staticText.setHeight(15);
                staticText.setX(((jasperDesign.getColumnWidth() / activeList.size()) * (activeList.indexOf(column) + 1)) - (jasperDesign.getColumnWidth() / activeList.size()));
                staticText.setY(0);
                staticText.setHorizontalTextAlign(HorizontalTextAlignEnum.getByName(column.getDataTextAlign()));
                staticText.setVerticalTextAlign(VerticalTextAlignEnum.getByName(column.getDataTextAlign()));
                staticText.setStyle(style2);
                staticText.getLineBox().getPen().setLineColor(Color.BLACK);
                staticText.getLineBox().getPen().setLineWidth(0.2f);
                staticText.setMode(ModeEnum.OPAQUE);
                staticText.setBackcolor(Color.LIGHT_GRAY);
                staticText.setText(column.getName());
                columnHeader.addElement(staticText);

                textField = new JRDesignTextField();
                textField.setWidth(jasperDesign.getColumnWidth() / activeList.size());
                textField.setHeight(15);
                textField.setX(((jasperDesign.getColumnWidth() / activeList.size()) * (activeList.indexOf(column) + 1)) - (jasperDesign.getColumnWidth() / activeList.size()));
                textField.setY(0);
                textField.setHorizontalTextAlign(HorizontalTextAlignEnum.getByName(column.getDataTextAlign()));
                textField.setVerticalTextAlign(VerticalTextAlignEnum.getByName(column.getDataTextAlign()));
                textField.setStyle(style2);
                textField.getLineBox().setPadding(5);
                textField.getLineBox().getPen().setLineColor(Color.BLACK);
                textField.getLineBox().getPen().setLineWidth(0.2f);
                textField.setExpression(new JRDesignExpression(column.getExpression()));
                textField.setStretchWithOverflow(true);
                textField.setBlankWhenNull(true);
                textField.setStretchType(StretchTypeEnum.ELEMENT_GROUP_HEIGHT);

                detail.addElement(textField);
            }

            if (column.isGroupBy()) {
                createGroupByBand(column, groupVariablesList);
            }

            if (column.isSortBy()) {
                jasperDesign.getSortFieldsList().add(createSortField(column, SortOrderEnum.DESCENDING));
            }

        }


        jasperDesign.setColumnHeader(columnHeader);

        ((JRDesignSection) jasperDesign.getDetailSection()).addBand(detail);

        //START COLUMN FOOTER
        frame = new JRDesignFrame();
        frame.setWidth(jasperDesign.getColumnWidth());
        frame.setHeight(15);
        frame.getLineBox().getBottomPen().setLineColor(Color.BLACK);
        frame.getLineBox().getBottomPen().setLineWidth(1);
        frame.getLineBox().setPadding(0);

        //Create Variables that will depends on this table
        ListIterator<Variable> listIterator = tableVariablesList.listIterator();
        while (listIterator.hasNext()) {
            Variable variable = listIterator.next();
            JRDesignVariable jrDesignVariable = new JRDesignVariable();
            jrDesignVariable.setName(variable.getExpression().toUpperCase() + "_" + variable.getOperation().toUpperCase() + "_ALL");
            jrDesignVariable.setValueClassName(Double.class.getName());
            jrDesignVariable.setCalculation(CalculationEnum.getByName(variable.getOperation()));
            jrDesignVariable.setExpression(new JRDesignExpression("$F{" + variable.getExpression() + "}"));
            jrDesignVariable.setIncrementType(IncrementTypeEnum.NONE);
            jrDesignVariable.setResetType(ResetTypeEnum.REPORT);
            jasperDesign.addVariable(jrDesignVariable);

            textField = new JRDesignTextField();
            textField.setWidth(jasperDesign.getColumnWidth() / tableVariablesList.size());
            textField.setHeight(15);
            textField.setX(((jasperDesign.getColumnWidth() / tableVariablesList.size()) * (tableVariablesList.indexOf(variable) + 1)) - (jasperDesign.getColumnWidth() / tableVariablesList.size()));
            textField.setY(0);
            textField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
            textField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
            textField.setStyle(style1);
            textField.setBackcolor(Color.CYAN);
            textField.setMode(ModeEnum.OPAQUE);
            textField.getLineBox().getPen().setLineColor(Color.BLACK);
            textField.getLineBox().getPen().setLineWidth(0.2f);
            textField.getLineBox().setPadding(1);
            textField.setExpression(new JRDesignExpression("\" " + variable.getName() + " : \" " + "+" + "new java.text.DecimalFormat(\"#.##\").format($V{" + variable.getExpression().toUpperCase() + "_" + variable.getOperation().toUpperCase() + "_ALL" + "})"));
            frame.addElement(textField);
        }

//        JRDesignVariable variable = new JRDesignVariable();
//        variable.setName("AMOUNT_SUM_ALL");
//        variable.setValueClassName(Double.class.getName());
//        variable.setCalculation(CalculationEnum.SUM);
//        variable.setExpression(new JRDesignExpression("$F{amount}"));
//        variable.setIncrementType(IncrementTypeEnum.NONE);
//        variable.setResetType(ResetTypeEnum.REPORT);
//        jasperDesign.addVariable(variable);


        JRDesignBand columnFooter = new JRDesignBand();
        columnFooter.setHeight(15);
        columnFooter.addElement(frame);
        //END COLUMN FOOTER

        jasperDesign.setColumnFooter(columnFooter);
    }

    private void createPageFooter() {
        band = new JRDesignBand();
        band.setHeight(60);

        frame = new JRDesignFrame();
        frame.setWidth(jasperDesign.getColumnWidth());
        frame.setHeight(band.getHeight());
        frame.getLineBox().getTopPen().setLineColor(Color.BLACK);
        frame.getLineBox().getTopPen().setLineWidth(1);
        frame.getLineBox().setPadding(0);

        band.addElement(frame);

        jasperDesign.setPageFooter(band);
    }

    private void createBackground() {
        band = new JRDesignBand();
        band.setHeight(jasperDesign.getPageHeight() - jasperDesign.getBottomMargin() - jasperDesign.getTopMargin());

        JRDesignRectangle rectangle = new JRDesignRectangle();
        rectangle.setWidth(jasperDesign.getColumnWidth());
        rectangle.setHeight(band.getHeight());
        rectangle.setMode(ModeEnum.TRANSPARENT);
        rectangle.setRadius(10);
        rectangle.setX(0);
        rectangle.getLinePen().setLineWidth(2);
        rectangle.getLinePen().setLineColor(Color.BLACK);
        rectangle.getLinePen().setLineStyle(LineStyleEnum.SOLID);
        band.addElement(rectangle);

        jasperDesign.setBackground(band);
    }

    private void createPageHeader(String reportTitle) {
        band = new JRDesignBand();
        band.setHeight(60);

        frame = new JRDesignFrame();
        frame.setWidth(jasperDesign.getColumnWidth());
        frame.setHeight(band.getHeight());
        frame.getLineBox().getBottomPen().setLineColor(Color.BLACK);
        frame.getLineBox().getBottomPen().setLineWidth(1);
        frame.getLineBox().setPadding(0);

        StringBuilder builder = new StringBuilder();
        builder.append("المملكة العربية السعودية");
        builder.append("\n");
        builder.append("المعهد الأهلي العالي للتدريب");
        builder.append("\n");
        builder.append("تحت إشراف المؤسسة العامة للتدريب المهني والتقني");

        staticText = new JRDesignStaticText();
        staticText.setWidth(jasperDesign.getColumnWidth() / 2);
        staticText.setHeight(45);
        staticText.setX((jasperDesign.getColumnWidth() - staticText.getWidth()) / 2);
        staticText.setY(0);
        staticText.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
        staticText.setStyle(style1);
        staticText.setText(builder.toString());
        frame.addElement(staticText);

        staticText = new JRDesignStaticText();
        staticText.setWidth(jasperDesign.getColumnWidth());
        staticText.setHeight(15);
        staticText.setX(0);
        staticText.setY(45);
        staticText.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
        staticText.setStyle(style2);
        staticText.setText(reportTitle);
        frame.addElement(staticText);

        image = new JRDesignImage(jasperDesign);
        JRDesignExpression expression = new JRDesignExpression();
        expression.setText("\"https://www.dropbox.com/s/bwbavw793i1hejf/LOGO.bmp?raw=1\"");
        image.setExpression(expression);
        image.setHorizontalImageAlign(HorizontalImageAlignEnum.CENTER);
        image.setVerticalImageAlign(VerticalImageAlignEnum.MIDDLE);
        image.setUsingCache(true);
        image.setFill(FillEnum.SOLID);
        image.setScaleImage(ScaleImageEnum.FILL_FRAME);
        image.setLazy(true);
        image.setHeight(35);
        image.setWidth(70);
        image.setX(5);
        image.setY(5);
        frame.addElement(image);

        band.addElement(frame);

        jasperDesign.setPageHeader(band);
    }

    private void createStyle() throws JRException {
        style1 = new JRDesignStyle();
        style1.setName("Style1");
        style1.setDefault(true);
        style1.setFontName("Arial");
        style1.setFontSize(11f);
        style1.getParagraph().setLineSpacing(LineSpacingEnum.SINGLE);
        style1.getParagraph().setLeftIndent(5);
        style1.getParagraph().setRightIndent(5);
        style1.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
        style1.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
        style1.setRotation(RotationEnum.NONE);
        style1.setBlankWhenNull(true);
        style1.getParagraph().setLineSpacing(LineSpacingEnum.SINGLE);
        jasperDesign.addStyle(style1);

        style2 = new JRDesignStyle();
        style2.setName("Style2");
        style2.setDefault(true);
        style2.setFontName("Arial");
        style2.setFontSize(10f);
        style2.getParagraph().setLineSpacing(LineSpacingEnum.SINGLE);
        style2.getParagraph().setLeftIndent(5);
        style2.getParagraph().setRightIndent(5);
        style2.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
        style2.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
        style2.setRotation(RotationEnum.NONE);
        style2.setBlankWhenNull(true);
        jasperDesign.addStyle(style2);
    }

    private void createGroupByBand(Column column, List<Variable> variables) throws JRException {

        //START HEADER
        JRDesignFrame headerFrame = new JRDesignFrame();
        headerFrame.setWidth(jasperDesign.getColumnWidth());
        headerFrame.setBackcolor(Color.decode("#E0E0E0"));
        headerFrame.setMode(ModeEnum.OPAQUE);
        headerFrame.getLineBox().getLeftPen().setLineWidth(2f);
        headerFrame.getLineBox().getLeftPen().setLineColor(Color.black);
        headerFrame.getLineBox().getRightPen().setLineWidth(2f);
        headerFrame.getLineBox().getRightPen().setLineColor(Color.black);
        headerFrame.setHeight(15);

        textField = new JRDesignTextField();
        textField.setWidth(jasperDesign.getColumnWidth());
        textField.setHeight(15);
        textField.setX(0);
        textField.setY(0);
        textField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
        textField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
        textField.setStyle(style2);
        textField.getLineBox().getPen().setLineWidth(0);
        textField.setExpression(new JRDesignExpression("\" " + column.getName() + " : \" " + "+" + column.getExpression()));
        headerFrame.addElement(textField);
        //END HEADER

        //START FOOTER
        JRDesignFrame footerFrame = new JRDesignFrame();
        footerFrame.setWidth(jasperDesign.getColumnWidth());
        footerFrame.setHeight(15);

        ListIterator<Variable> listIterator = variables.listIterator();
        while (listIterator.hasNext()) {
            Variable variable = listIterator.next();
            textField = new JRDesignTextField();
            textField.setWidth(jasperDesign.getColumnWidth() / variables.size());
            textField.setHeight(15);
            textField.setX(((jasperDesign.getColumnWidth() / variables.size()) * (variables.indexOf(variable) + 1)) - (jasperDesign.getColumnWidth() / variables.size()));
            textField.setY(0);
            textField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
            textField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
            textField.setStyle(style1);
            textField.setBackcolor(Color.LIGHT_GRAY);
            textField.setMode(ModeEnum.OPAQUE);
            textField.getLineBox().getPen().setLineColor(Color.BLACK);
            textField.getLineBox().getPen().setLineWidth(0.2f);
            textField.getLineBox().setPadding(1);
            textField.setExpression(new JRDesignExpression("\" " + variable.getName() + " : \" " + "+" + "new java.text.DecimalFormat(\"#.##\").format($V{" + variable.getExpression().toUpperCase() + "_" + variable.getOperation().toUpperCase() + "})"));
            footerFrame.addElement(textField);
        }
        //END FOOTER

        //Create Group
        JRDesignGroup group = new JRDesignGroup();

        //Create Variables that will depends on this group
        ListIterator<Variable> listIterator1 = variables.listIterator();
        while (listIterator1.hasNext()) {
            Variable variable = listIterator1.next();
            JRDesignVariable jrDesignVariable = new JRDesignVariable();
            jrDesignVariable.setName(variable.getExpression().toUpperCase() + "_" + variable.getOperation().toUpperCase());
            jrDesignVariable.setValueClassName(Double.class.getName());
            jrDesignVariable.setCalculation(CalculationEnum.getByName(variable.getOperation()));
            jrDesignVariable.setExpression(new JRDesignExpression("$F{" + variable.getExpression() + "}"));
            jrDesignVariable.setIncrementType(IncrementTypeEnum.NONE);
            jrDesignVariable.setResetType(ResetTypeEnum.GROUP);
            jrDesignVariable.setResetGroup(group);
            jasperDesign.addVariable(jrDesignVariable);
        }

        group.setName(column.getName());
        group.setKeepTogether(true);
        group.setMinHeightToStartNewPage(60);
        group.setExpression(new JRDesignExpression(column.getExpression()));

        //Create variable for count groups
        JRDesignVariable groupCountVariable = new JRDesignVariable();
        groupCountVariable.setName(group.getName() + "_COUNT_NUMBER");
        groupCountVariable.setValueClassName(Integer.class.getName());
        groupCountVariable.setCalculation(CalculationEnum.DISTINCT_COUNT);
        groupCountVariable.setExpression(new JRDesignExpression(column.getExpression()));
        groupCountVariable.setIncrementType(IncrementTypeEnum.GROUP);
        groupCountVariable.setIncrementGroup(group);
        groupCountVariable.setResetType(ResetTypeEnum.REPORT);
        jasperDesign.addVariable(groupCountVariable);

        //Create conditional style for this group
        JRDesignConditionalStyle conditionalStyle1 = new JRDesignConditionalStyle();
        conditionalStyle1.setConditionExpression(new JRDesignExpression("new Boolean($V{" + groupCountVariable.getName() + "}.intValue() % 2 ==0 ) == false"));
        conditionalStyle1.setMode(ModeEnum.OPAQUE);
        conditionalStyle1.setBackcolor(Color.GREEN);
        JRDesignConditionalStyle conditionalStyle2 = new JRDesignConditionalStyle();
        conditionalStyle2.setConditionExpression(new JRDesignExpression("new Boolean($V{" + groupCountVariable.getName() + "}.intValue() % 2 ==0 ) == true"));
        conditionalStyle2.setMode(ModeEnum.OPAQUE);
        conditionalStyle2.setBackcolor(Color.GREEN);

        JRDesignStyle groupStyle = new JRDesignStyle();
        groupStyle.setName(group.getName() + "_STYLE");
        groupStyle.getConditionalStyleList().add(conditionalStyle1);
        groupStyle.getConditionalStyleList().add(conditionalStyle2);
        jasperDesign.addStyle(groupStyle);

//        headerFrame.setStyle(groupStyle);
        JRDesignBand header = new JRDesignBand();
        header.setHeight(15);
        header.addElement(headerFrame);
        Optional.ofNullable(header).ifPresent(value -> {
            if (value.getElements().length > 0) {
                ((JRDesignSection) group.getGroupHeaderSection()).addBand(value);
            }
        });
//        footerFrame.setStyle(groupStyle);
        JRDesignBand footer = new JRDesignBand();
        footer.setHeight(15);
        if (footerFrame.getElements().length > 0) {
            footer.addElement(footerFrame);
        }
        Optional.ofNullable(footer).ifPresent(value -> {
            if (value.getElements().length > 0) {
                ((JRDesignSection) group.getGroupFooterSection()).addBand(value);
            }
        });
        jasperDesign.addGroup(group);
    }

    private JRDesignSortField createSortField(Column column, SortOrderEnum order) throws JRException {
        JRDesignVariable variable = new JRDesignVariable();
        variable.setName(column.getValue());
        variable.setValueClassName(column.getValueClassName());
        variable.setExpression(new JRDesignExpression(column.getExpression()));
        jasperDesign.getVariablesList().add(variable);

        JRDesignSortField sortField = new JRDesignSortField();
        sortField.setName(variable.getName());
        sortField.setType(SortFieldTypeEnum.VARIABLE);
        sortField.setOrder(order);
        return sortField;
    }

    public void export(final String exportType, final HttpServletResponse response, final OutputStream ouputStream, final JasperPrint jasperPrint) throws JRException, IOException {

        Exporter exporter = null;

        switch (exportType) {
            case "pdf":
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "inline; filename=\"report.pdf\"");
                exporter = new JRPdfExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(ouputStream));
                SimplePdfExporterConfiguration configurationPdf = new SimplePdfExporterConfiguration();
                configurationPdf.setCreatingBatchModeBookmarks(true);
                exporter.setConfiguration(configurationPdf);
                break;
            case "rtf":
                response.setContentType("application/rtf");
                response.setHeader("Content-Disposition", "inline; filename=\"report.rtf\"");
                exporter = new JRRtfExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleWriterExporterOutput(ouputStream));
                break;
            case "html":
                response.setContentType("application/html");
                response.setHeader("Content-Disposition", "inline; filename=\"report.html\"");
                exporter = new HtmlExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleHtmlExporterOutput(ouputStream));
                SimpleHtmlReportConfiguration reportExportConfiguration = new SimpleHtmlReportConfiguration();
                reportExportConfiguration.setWhitePageBackground(false);
                reportExportConfiguration.setRemoveEmptySpaceBetweenRows(true);
                exporter.setConfiguration(reportExportConfiguration);
                break;
            case "xhtml":
                response.setContentType("application/xhtml");
                response.setHeader("Content-Disposition", "inline; filename=\"report.xhtml\"");
                exporter = new HtmlExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleHtmlExporterOutput(ouputStream));
                break;
            case "xlsx":
                response.setContentType("application/xlsx");
                response.setHeader("Content-Disposition", "inline; filename=\"report.xlsx\"");
                exporter = new JRXlsxExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(ouputStream));
                SimpleXlsxReportConfiguration configurationXlsx = new SimpleXlsxReportConfiguration();
                configurationXlsx.setOnePagePerSheet(false);
                exporter.setConfiguration(configurationXlsx);
                break;
            case "csv":
                response.setContentType("application/csv");
                response.setHeader("Content-Disposition", "inline; filename=\"report.csv\"");
                exporter = new JRCsvExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleWriterExporterOutput(ouputStream));
                break;
            case "pptx":
                response.setContentType("application/pptx");
                response.setHeader("Content-Disposition", "inline; filename=\"report.pptx\"");
                exporter = new JRPptxExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(ouputStream));
                break;
            case "docx":
                response.setContentType("application/docx");
                response.setHeader("Content-Disposition", "inline; filename=\"report.docx\"");
                exporter = new JRDocxExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(ouputStream));
                break;
            case "ods":
                response.setContentType("application/ods");
                response.setHeader("Content-Disposition", "inline; filename=\"report.ods\"");
                exporter = new JROdsExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(ouputStream));
                SimpleOdsReportConfiguration configurationOds = new SimpleOdsReportConfiguration();
                configurationOds.setOnePagePerSheet(true);
                exporter.setConfiguration(configurationOds);
                break;
            case "odt":
                response.setContentType("application/odt");
                response.setHeader("Content-Disposition", "inline; filename=\"report.odt\"");
                exporter = new JROdtExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(ouputStream));
                break;

        }

        exporter.exportReport();
        ouputStream.flush();
        ouputStream.close();
    }

    @RequestMapping(
            value = "/report/dynamic/filteredTasks",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.ALL_VALUE)
    public void ReportFilteredTasks(
            @RequestBody ReportProp reportProp,
            @RequestParam(value = "title", required = false) final String title,
            @RequestParam(value = "codeFrom", required = false) final Long codeFrom,
            @RequestParam(value = "codeTo", required = false) final Long codeTo,
            @RequestParam(value = "startDateFrom", required = false) final Long startDateFrom,
            @RequestParam(value = "startDateTo", required = false) final Long startDateTo,
            @RequestParam(value = "endDateFrom", required = false) final Long endDateFrom,
            @RequestParam(value = "endDateTo", required = false) final Long endDateTo,
            @RequestParam(value = "taskType", required = false) final Boolean taskType,
            @RequestParam(value = "isTaskOpen", required = false) final Boolean isTaskOpen,
            @RequestParam(value = "timeType", required = false) final String timeType,
            @RequestParam(value = "person", required = false) final Long person,
            HttpServletResponse response
    ) throws Exception {
        List<TaskOperation> list = new ArrayList<>();
        taskSearch.search(title, codeFrom, codeTo, startDateFrom, startDateTo, endDateFrom, endDateTo, taskType, isTaskOpen, timeType, person).stream().forEach(task -> list.addAll(task.getTaskOperations()));
        if(list.isEmpty()){
            throw  new CustomException("لا توجد حراكات او تعليقات على المهام المفلترة للطباعة");
        }
        initTasksTableColumns(reportProp);
        JasperDesign jasperDesign = getJasperDesign(reportProp.getTitle(), reportProp.getOrientation(), reportProp.getColumns(), new ArrayList<>(), new ArrayList<>());
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, new JRBeanCollectionDataSource(list));
        export(reportProp.getExportType(), response, response.getOutputStream(), jasperPrint);
    }

    @RequestMapping(
            value = "/report/dynamic/task",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.ALL_VALUE)
    public void ReportTask(
            @RequestBody ReportProp reportProp,
            @RequestParam(value = "id") final Long id,
            HttpServletResponse response
    ) throws Exception {
        initTasksTableColumns(reportProp);
        List<TaskOperation> taskOperationList = taskService.findOne(id).getTaskOperations();
        if(taskOperationList.isEmpty()){
            throw new CustomException("لا تحتوى هذة المهمة على حركات حتى الآن، فضلاً تأكد من وجود حركات أو تعليقات لطباعة التقارير");
        }
        JasperDesign jasperDesign = getJasperDesign(reportProp.getTitle(), reportProp.getOrientation(), reportProp.getColumns(), new ArrayList<>(), new ArrayList<>());
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, new JRBeanCollectionDataSource(taskOperationList));
        export(reportProp.getExportType(), response, response.getOutputStream(), jasperPrint);
    }

    private void initTasksTableColumns(@RequestBody ReportProp reportProp) {
        reportProp.getColumns().stream().forEach(col -> {
            if (col.getName().equals("رقم المهمة")) {
                col.setValue("task");
                col.setExpression("$F{task}.getCode()");
                col.setValueClassName(Task.class.getName());
            }
            if (col.getName().equals("عنوان المهمة")) {
                col.setValue("task");
                col.setExpression("$F{task}.getTitle()");
                col.setValueClassName(Task.class.getName());
            }
            if (col.getName().equals("تفاصيل المهمة")) {
                col.setValue("task");
                col.setExpression("$F{task}.getContent()");
                col.setValueClassName(Task.class.getName());
            }
            if (col.getName().equals("تاريخ إنشاء المهمة")) {
                col.setValue("task");
                col.setExpression("com.besafx.app.util.DateConverter.getHijriStringFromDateRTL($F{task}.getStartDate())");
                col.setValueClassName(Task.class.getName());
            }
            if (col.getName().equals("تاريخ تسليم المهمة")) {
                col.setValue("task");
                col.setExpression("com.besafx.app.util.DateConverter.getHijriStringFromDateRTL($F{task}.getEndDate())");
                col.setValueClassName(Task.class.getName());
            }
            if (col.getName().equals("جهة التكليف")) {
                col.setValue("task");
                col.setExpression("$F{task}.getPerson().getName()");
                col.setValueClassName(Task.class.getName());
            }
            if (col.getName().equals("رقم الحركة")) {
                col.setValue("code");
                col.setExpression("$F{code}");
                col.setValueClassName(Integer.class.getName());
            }
            if (col.getName().equals("تاريخ الحركة")) {
                col.setValue("date");
                col.setExpression("com.besafx.app.util.DateConverter.getTimeFromDate($F{date}) + \" \" + com.besafx.app.util.DateConverter.getHijriStringFromDateRTL($F{date})");
                col.setValueClassName(Date.class.getName());
            }
            if (col.getName().equals("محتوى الحركة")) {
                col.setValue("content");
                col.setExpression("$F{content}");
                col.setValueClassName(String.class.getName());
            }
            if (col.getName().equals("مدخل الحركة")) {
                col.setValue("sender");
                col.setExpression("$F{sender}.getName()");
                col.setValueClassName(Person.class.getName());
            }
        });
    }

}
