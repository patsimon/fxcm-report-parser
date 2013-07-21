import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReportParser {

    Trade trade;
    List<Trade> trades = new ArrayList<Trade>();

    public ReportParser(Document doc) {
        Elements elems = doc.getElementsByAttributeValue("name", "closed_trades");
        if (elems.size() > 1) {
            log("More than one main tables");
        }
        Element tbody = elems.get(0).getElementsByTag("tbody").get(0);
        Elements tableRows = tbody.getElementsByTag("tr");
        parseRows(tableRows);
    }

    private void parseRows(Elements tableRows) {
        int counter = 0;
        for (Element row : tableRows) {
            counter++;
            if (counter < 7) continue;
            if (row.getElementsByTag("td").size() != 13) continue;
            if (counter % 2 == 1) {
                trade = parseOpenTrade(row);
            } else {
                parseCloseTrade(trade, row);
                trades.add(trade);
            }
        }
        printSqls();
    }

    private void printSqls() {
        for(Trade trade:trades) {
            System.out.println(trade.toInsertSql());
        }
    }

    private Trade parseOpenTrade(Element row) {
        return new Trade(row);
    }

    private void log(String msg) {

    }

    private void parseCloseTrade(Trade trade, Element row) {
        Elements cells = row.getElementsByTag("td");
        trade.setClosed(cells.get(3).text());
        trade.setProfitLoss(cells.get(6).text());
        if (trade.getSold() == null) {
            trade.setTradeType(Trade.TradeType.L);
            trade.setSold(cells.get(4).text());
        }
        if (trade.getBought() == null) {
            trade.setTradeType(Trade.TradeType.S);
            trade.setBought(cells.get(5).text());
        }
    }

    public static void main(String[] args) {
        Document doc = null;
        try {
            File report = new File(args[0]);
            doc = Jsoup.parse(report, "UTF-8", "");
        } catch (Exception ex) {
            System.err.print("First arg must be the location of the report");
        }
        ReportParser parser = new ReportParser(doc);
    }
}
