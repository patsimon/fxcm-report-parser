import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Trade {


    enum TradeType {
        L, S
    }

    public TradeType getTradeType() {
        return tradeType;
    }

    public void setTradeType(TradeType tradeType) {
        this.tradeType = tradeType;
    }

    private TradeType tradeType;

    public Trade(Element row) {
        Elements cells = row.getElementsByTag("td");
        this.ticketNumber = cells.get(0).text();
        this.symbol = getCellText(cells, 1);
        this.volume = getInteger(getCellText(cells, 2));
        try {
            this.opened = SDF.parse(getCellText(cells, 3));
        } catch (ParseException pe) {
            System.err.println("Cannot parse dates:" + pe.getMessage());
        }
        this.sold = getBigDecimal(getCellText(cells, 4));
        this.bought = getBigDecimal(getCellText(cells, 5));

        this.profitLoss = getBigDecimal(getCellText(cells, 6));
    }

    private int getInteger(String text) {
        text = text.replace(",", "");
        return Integer.parseInt(text);
    }

    private BigDecimal getBigDecimal(String content) {
        try {
            content = content.replace(",", "");
            return new BigDecimal(content);
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    public String getCellText(Elements cells, int position) {
        return cells.get(position).text();
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public Date getOpened() {
        return opened;
    }

    public void setOpened(Date opened) {
        this.opened = opened;
    }

    public Date getClosed() {
        return closed;
    }

    public void setClosed(Date closed) {
        this.closed = closed;
    }

    public BigDecimal getBought() {
        return bought;
    }

    public void setBought(BigDecimal bought) {
        this.bought = bought;
    }

    public BigDecimal getSold() {
        return sold;
    }

    public void setSold(BigDecimal sold) {
        this.sold = sold;
    }

    public BigDecimal getProfitLoss() {
        return profitLoss;
    }

    public void setProfitLoss(BigDecimal profitLoss) {
        this.profitLoss = profitLoss;
    }

    int volume;
    String symbol;
    String ticketNumber;
    Date opened;
    Date closed;
    BigDecimal bought;
    BigDecimal sold;
    BigDecimal profitLoss;

    public void setClosed(String text) {
        try {
            this.closed = SDF.parse(text);
        } catch (ParseException pe) {
            System.err.println("Cannot parse dates:" + pe.getMessage());
        }
    }

    public void setProfitLoss(String text) {
        this.profitLoss = getBigDecimal(text);
    }

    public void setSold(String text) {
        this.sold = getBigDecimal(text);
    }

    public void setBought(String text) {
        this.bought = getBigDecimal(text);
    }

    static String INSERT_SQL = "Insert into positions (dateOpen, dateClosed, product, LongShort, QTY, BAUGHT, SOLD) " +
            " values('%s', '%s', '%s', '%s', %d, %f, %f);";

    static SimpleDateFormat SDF = new SimpleDateFormat("M/d/yy h:mm a");
    //2013-07-16 22:22:00
    static String MYSQL_DATETIME = "yyyy-MM-dd kk:mm:ss";

    static SimpleDateFormat MySDF = new SimpleDateFormat(MYSQL_DATETIME);


    public String toInsertSql() {
        return String.format(INSERT_SQL, MySDF.format(this.opened), MySDF.format(this.closed), this.symbol
                , this.getTradeType(), this.volume, this.bought, this.sold);
    }
}


