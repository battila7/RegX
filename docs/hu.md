## Beépített eszközök

A futtató környezet biztosít különböző meghívható függvényeket a felhasználók számára,
hogy megkönnyítse a reguláris kifejezésekkel és a sztringekkel való munkát.

### Sztringek és reguláris kifejezések kiíratása
Alapvető beépített függvény a print, mely sztring típusú változókat képes az outputra továbbítani.
A regex típusokat előbb szöveggé kell konvertálni - erre szolgál az asText függvény-,
csak ezt követően lehet a print függvényt alkalmazni.
Példa program:
~~~~
regex a = /ab*+cd/;
string s = "alma";

print(s);
print(asText(a));
~~~~

### Véges automata generálása
A deklarált nyelvtan által felírható reguláris kifejezésekhez lehet véges automatát generálni, mely
nemdeterminisztikus, de a reguláris kifejezéssel ekvivalens.
Példa program:
~~~~
regex r = /(a+b)*/;
print_automaton(r);
~~~~

Amelynek kimenete:
~~~~
Automaton:
--------
2 | \ | 3
--------
3 | a | 4
--------
2 | \ | 5
--------
5 | b | 6
--------
4 | \ | 7
--------
6 | \ | 7
--------
7 | \ | 2
--------
1 | \ | 2
--------
7 | \ | 8
--------
1 | \ | 8
--------
Starting state: 1
Accepting states: 8
~~~~

### Unió normálforma
A reguláris kifejezéseket beépített függvény segítségével unió normálformára is hozhatjuk.
Ehhez a négy fő átalakítási szabályt alkalmazza a függvény:
1. (p+r)* -> (p*r*)*
2. p(q+r) -> pq+pr
3. (p+q)r -> pr+qr
4. (p+q)(r+t) -> pr+pt+qr+qt

Példa program:
~~~~
regex a = /(a+b)*(c+d+e)/;
regex b = normalize(a);
print(asText(b));
~~~~

### Egyszerűsítés
A reguláris kifejezéseket bizonyos szabályok alapján egyszerűsíteni is lehet.
Ilyen átalakítások lehetnek:
1. Fölösleges zárójelek elhagyása: (a) -> a és ((a+b))* -> (a+b)*
2. Többszörös * használata: (a*)* -> a*
3. Azonos komponensek egy unión beül: a+b+a -> a+b

~~~~
regex a = /(a)+(c*)*+bd+a+c*/;
regex s = simplify(a);
print(asText(s));
~~~~

Amelynek kimenete:
~~~~
a+c*+bd
~~~~