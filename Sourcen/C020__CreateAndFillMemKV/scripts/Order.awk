# INSERT INTO [Order] ([Id],[OrderDate],[CustomerId],[TotalAmount],[OrderNumber])VALUES(796,'Apr 22 2014 12:00:00:000AM',74,210.00,'543173')

BEGIN {
    runs = 0
}

END {
    print "Processed " runs " records"
}

{
    #

    runs++

    lw = split($0, words, " ")

    # 1 INSERT
    # 2 INTO
    # 3 OrderItem
    # 4 (Id,OrderId,ProductId,UnitPrice,Quantity)VALUES(30,11,2,15.20,50)
    # Brackets already removed

    tableName = removeBrackets(words[3]) "s"

    lav = split( removeBrackets(words[4]), attrVals, "VALUES")
    ltemp = split( removeBrackets($0), avTemp, "VALUES")
    # There are spaces in the values
    attrVals[2] = avTemp[ltemp]

    # 1 (Id,OrderId,ProductId,UnitPrice,Quantity)
    # 2 (30,11,2,15.20,50)
    # Braces need to be removed

    la = split( removeBraces(attrVals[1]), attr, ",")

    # 1 Id
    # 2 OrderId
    # 3 ProductId
    # 4 UnitPrice
    # 5 Quantity

    lv = split( removeBraces(attrVals[2]), vals, ",")

    # 1 30
    # 2 11
    # 3 2
    # 4 15.20
    # 5 50

    print "tempMkv = new MemKV();"
    for (i = 1; i <= lv; i++) {
        if( i != 2 ) {
            print "tempMkv.put(\"" attr[i] "\", " replaceTicks(vals[i]) " );"
        } else {
            split( replaceTicks(vals[i]), arr, " ")
            print "tempMkv.put(\"" attr[i] "\", new Date( "  arr[2] ", " getMonthVal( arr[1] ) ", " arr[2] " ) );"
        }
    }
    print "mkv.put(\"" tableName "/" vals[1] "\", tempMkv);"
    print ""
    print ""
}

function removeBrackets( s ) {
    gsub(/[[\]]/, "", s)
    return s
}

function removeBraces( s ) {
    # gsub(/[\()]/, "", s)
    sub(/^[ \t]*\(/, "", s)
    sub(/\)[ \t]*$/, "", s)
    return s
}

function replaceTicks( s ) {
     gsub(/[']/, "\"", s)
    # sub(/^[ \t]*\(/, "", s)
    # sub(/\)[ \t]*$/, "", s)
    return s
}

function getMonthVal( str ) {
    retVal = 0;
    sub( /"/, "", str)
    if( "Jan"  ==  str )  {  retVal =  0; } else
    if( "Feb"  ==  str )  {  retVal =  1; } else
    if( "Mar"  ==  str )  {  retVal =  2; } else
    if( "Apr"  ==  str )  {  retVal =  3; } else
    if( "May"  ==  str )  {  retVal =  4; } else
    if( "Jun"  ==  str )  {  retVal =  5; } else
    if( "Jul"  ==  str )  {  retVal =  6; } else
    if( "Aug"  ==  str )  {  retVal =  7; } else
    if( "Sep"  ==  str )  {  retVal =  8; } else
    if( "Oct"  ==  str )  {  retVal =  9; } else
    if( "Nov"  ==  str )  {  retVal = 10; } else
    if( "Dec"  ==  str )  {  retVal = 11; }

    return retVal;
}