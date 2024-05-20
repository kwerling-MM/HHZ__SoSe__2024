# INSERT INTO [Product] ([Id],[ProductName],[SupplierId],[UnitPrice],[Package],[IsDiscontinued])VALUES(47,'Zaanse koeken',22,9.50,'10 - 4 oz boxes',0)

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
        print "tempMkv.put(\"" attr[i] "\", " replaceTicks(vals[i]) ");"
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