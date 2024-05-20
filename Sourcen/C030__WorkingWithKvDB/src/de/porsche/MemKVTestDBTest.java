package de.porsche;

import static org.junit.jupiter.api.Assertions.*;

import com.kwerlingit.MemKV;
import de.porsche.NewMemKVDB;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

class MemKVTestDBTest {

    MemKV memKV = null;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        memKV = new NewMemKVDB().get();
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        memKV = null;
    }

    @org.junit.jupiter.api.Test
    void countCustomers() {
        // Select count(*) from customers;
        int count = memKV.findEntriesByKey("Customers/*").size();
        assertEquals( 91, count );
    }

    /*
    show the top 3 order numbers and the order item counts for these orders in descending order

    SELECT
  o.OrderNumber,
  COUNT(oi.Id) AS OrderItemCount
FROM Orders o
JOIN OrderItems oi ON o.Id = oi.OrderId
GROUP BY o.OrderNumber
ORDER BY OrderItemCount DESC
LIMIT 3;
     */

    @org.junit.jupiter.api.Test
    void countCustomersFromAustria() {
        // Select count(*) from customers where country = 'Austria';
        MemKV tm = memKV.findEntriesByKey("Customers/*");
        List<String> keys = tm.getListOfKeys();
        int count = 0;
        for( String key : keys ) {
            MemKV tc = (MemKV) tm.get(key);
            if ("Austria" == tc.get("Country")) {
                count++;
            }
        }
        assertEquals( 2, count );
     }



    @org.junit.jupiter.api.Test
    void showTop3CustomersBasedOnOrdervolume() {

        /*
                SELECT
                    c.FirstName,
                    c.LastName,
                    SUM(oi.Quantity * oi.UnitPrice) AS TotalOrderVolume
                FROM Customers c
                    JOIN Orders o ON c.Id = o.CustomerId
                    JOIN OrderItems oi ON o.Id = oi.OrderId
                GROUP BY c.Id
                ORDER BY TotalOrderVolume DESC
                LIMIT 3;
         */

        class UidTotal {
            private int uID;
            private double total;

            public UidTotal(int uID, double total) {
                this.uID = uID;
                this.total = total;
            }

            public int getUser()                { return uID;           }

            public void setUID(int uID)         { this.uID = uID;       }

            public double getTotal()            { return total;         }

            public void setTotal(double total)  { this.total = total;   }

            @Override
            public String toString() {
                return "UserTotal{" + "user=" + uID + ", total=" + total + '}';
            }
        }


        MemKV tc  = memKV.findEntriesByKey("Customers/*");
        MemKV to  = memKV.findEntriesByKey("Orders/*");
        MemKV toi = memKV.findEntriesByKey("OrderItems/*");
        List<String> keysC = tc.getListOfKeys();
        List<String> keysO = to.getListOfKeys();
        List<String> keysOI = toi.getListOfKeys();

        List<UidTotal> utList = new ArrayList<>();

        for( String keyC : keysC ) {
            int cID = ((MemKV) tc.get(keyC)).getInteger("Id");
            UidTotal ut = new UidTotal(cID, 0);

            for( String keyO : keysO ) {
                if((Integer) ((MemKV) to.get(keyO)).get("CustomerId") ==  cID ) {
                    int oID = ((MemKV) to.get(keyO)).getInteger("Id");
                    for (String keyOI : keysOI) {
                        MemKV tMemKV = (MemKV) toi.get(keyOI);
                        if ((Integer) (tMemKV.getInteger("OrderId")) == oID) {
                            double price = tMemKV.getDouble("UnitPrice");
                            int quantity = tMemKV.getInteger("Quantity");
                            ut.setTotal(ut.getTotal() + quantity * price);
                        }
                    }
                }
            }
            utList.add(ut);
        }
        to = null;
        toi = null;

        // sort the list
        utList.sort(Comparator.comparing(UidTotal::getTotal).reversed());

        for( int i = 0; i < 3; i++ ) {
            UidTotal ut = utList.get( i );
            MemKV tm = tc.getMemKV( "Customers/" + ut.getUser() );
            String fName = tm.getString("FirstName");
            String lName = tm.getString("LastName");
            String tot = ((Double) ( Math.round( ut.getTotal() * 100 ) / 100.0 ) ).toString();
            System.out.printf("%10s %10s %10s\n", fName, lName, tot);

            if( 0 == i ){
                assertEquals("Horst", fName);
                assertEquals("Kloss", lName);
                assertEquals("117483.39", tot);
            } else if ( 1 == i ) {
                assertEquals("Jose", fName);
                assertEquals("Pavarotti", lName);
                assertEquals("115673.39", tot);
            } else {
                assertEquals("Roland", fName);
                assertEquals("Mendel", lName);
                assertEquals("113236.68", tot);
            }
        }

    }
}