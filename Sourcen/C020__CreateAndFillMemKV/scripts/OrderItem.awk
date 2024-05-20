# INSERT INTO [OrderItem] ([Id],[OrderId],[ProductId],[UnitPrice],[Quantity])VALUES(30,11,2,15.20,50)

BEGIN {
    runs = 0
}

END {
    print "Processed " runs " records"
}

/^INSERT INTO \[([^\]]+)\] \(([^\)]+)\)VALUES\(([^)]+)\)/ {
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
    t = "addOrderItemToKV( tempMkv"
    for (i = 1; i <= lv; i++) {
        # print "tempMkv.put(\"" attr[i] "\", " vals[i] ");"
        t = t ", " vals[i]
    }
    t = t " );"
    print t
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