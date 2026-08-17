package com.electricity;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/calculate")
public class ElectricityBillServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws IOException {

        response.setContentType("text/html;charset=UTF-8");

        String name = request.getParameter("consumerName");
        String number = request.getParameter("consumerNumber");

        double units;

        try {
            units = Double.parseDouble(request.getParameter("units"));
        } catch (Exception e) {
            response.sendError(400, "Invalid units entered");
            return;
        }

        if (units < 0) {
            response.sendError(400, "Units cannot be negative");
            return;
        }

        double first50 = Math.min(units, 50) * 3.50;

        double next100 = Math.min(
                Math.max(units - 50, 0), 100
        ) * 4.00;

        double next100_2 = Math.min(
                Math.max(units - 150, 0), 100
        ) * 5.20;

        double above250 = Math.max(units - 250, 0) * 6.50;

        double total = first50 + next100 + next100_2 + above250;

        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Electricity Bill</title>");
        out.println("<link rel='stylesheet' href='style.css'>");

        out.println("<style>");
        out.println(".bill{background:#fff;max-width:820px;margin:auto;padding:38px;border-radius:22px;box-shadow:0 15px 40px rgba(0,0,0,.08)}");
        out.println(".bill-header{display:flex;justify-content:space-between;border-bottom:1px solid #eee;padding-bottom:22px}");
        out.println(".bill-header h1{margin:8px 0}");
        out.println(".info{display:grid;grid-template-columns:repeat(3,1fr);gap:12px;margin:25px 0}");
        out.println(".info-box{background:#f6f8fb;padding:15px;border-radius:10px}");
        out.println(".info-box small{display:block;color:#8d96a5;font-size:9px;margin-bottom:7px}");
        out.println(".bill-row{display:flex;justify-content:space-between;padding:16px 0;border-bottom:1px solid #eee}");
        out.println(".bill-row small{display:block;color:#929aaa;margin-top:4px}");
        out.println(".total{margin-top:20px;background:#182338;color:#fff;padding:22px;border-radius:13px;display:flex;justify-content:space-between}");
        out.println(".total b{color:#ffd34e;font-size:27px}");
        out.println(".actions{display:flex;gap:12px;margin-top:20px}");
        out.println(".actions button,.actions a{flex:1;text-align:center;padding:13px;border-radius:10px;border:0;text-decoration:none;font-weight:800}");
        out.println(".print-btn{background:#5369e9;color:#fff}");
        out.println(".back-btn{background:#edf0f5;color:#445066}");
        out.println("@media(max-width:650px){.info{grid-template-columns:1fr}.bill{padding:22px}.actions{flex-direction:column}}");
        out.println("@media print{header,.actions{display:none!important}body{background:#fff}.bill{box-shadow:none;padding:0}.total{background:#eee;color:#000}.total b{color:#000}}");
        out.println("</style>");

        out.println("</head>");
        out.println("<body>");

        out.println("<header>");
        out.println("<b>PowerBill</b>");
        out.println("<span>BILL GENERATED</span>");
        out.println("</header>");

        out.println("<main>");

        out.println("<div class='bill'>");

        out.println("<div class='bill-header'>");
        out.println("<div>");
        out.println("<small>ELECTRICITY BILL</small>");
        out.println("<h1>Payment Summary</h1>");
        out.println("<p>Electricity Consumption Bill</p>");
        out.println("</div>");
        out.println("<b style='font-size:45px'>POWER</b>");
        out.println("</div>");

        out.println("<div class='info'>");

        out.println("<div class='info-box'>");
        out.println("<small>CONSUMER NAME</small>");
        out.println("<b>" + escape(name) + "</b>");
        out.println("</div>");

        out.println("<div class='info-box'>");
        out.println("<small>CONSUMER NUMBER</small>");
        out.println("<b>" + escape(number) + "</b>");
        out.println("</div>");

        out.println("<div class='info-box'>");
        out.println("<small>UNITS CONSUMED</small>");
        out.println("<b>" + format(units) + " kWh</b>");
        out.println("</div>");

        out.println("</div>");

        out.println("<h2>Charge Breakdown</h2>");

        addRow(out, "First 50 units", units, 0, 50, 3.50, first50);
        addRow(out, "Next 100 units", units, 50, 150, 4.00, next100);
        addRow(out, "Next 100 units", units, 150, 250, 5.20, next100_2);
        addRow(out, "Above 250 units", units, 250, Double.MAX_VALUE, 6.50, above250);

        out.println("<div class='total'>");
        out.println("<span>Total Amount Due</span>");
        out.println("<b>Rs. " + format(total) + "</b>");
        out.println("</div>");

        out.println("<div class='actions'>");

        out.println("<button class='print-btn' onclick='window.print()'>");
        out.println("Print Bill");
        out.println("</button>");

        out.println("<a class='back-btn' href='index.html'>");
        out.println("Calculate Again");
        out.println("</a>");

        out.println("</div>");

        out.println("</div>");

        out.println("</main>");
        out.println("</body>");
        out.println("</html>");
    }

    private void addRow(
            PrintWriter out,
            String title,
            double units,
            double min,
            double max,
            double rate,
            double amount) {

        if (amount <= 0) {
            return;
        }

        double used;

        if (max == Double.MAX_VALUE) {
            used = Math.max(units - min, 0);
        } else {
            used = Math.min(
                    Math.max(units - min, 0),
                    max - min
            );
        }

        out.println("<div class='bill-row'>");

        out.println("<span>");
        out.println("<b>" + title + "</b>");
        out.println("<small>"
                + format(used)
                + " units x Rs. "
                + format(rate)
                + "</small>");
        out.println("</span>");

        out.println("<b>Rs. " + format(amount) + "</b>");

        out.println("</div>");
    }

    private String format(double value) {
        return String.format("%.2f", value);
    }

    private String escape(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}