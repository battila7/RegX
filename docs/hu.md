# RegX

Reguláris kifejezéseket feldolgozó nyelv.

## Típusok

A nyelv háromféle típust támogat, melyek a következőek:

  * `string`: Karaktersorozat
  * `list`: Lista, melynek elemei `string` típusúak
  * `regex`: Reguláris kifejezés

## Változódeklarációk

Változót deklarálni a következő módon tudunk:

~~~~
  típus azonosító [ = kezdőérték];
~~~~

Az azonosító az angol ABC kis- és nagybetűivel, vagy a `$` és `_` karakterekkel kezdődhet és ugyanezekkel, valamint számokkal folytatódhat.

~~~~
  // szabályos
  string $str1;

  // szabálytalan
  string 12a;
~~~~

Példa különféle típusú változók deklarálására és inicializálásra:

~~~~
  string str = "Hello, World!";

  list lst = ["Hello", "World", "!"];

  regex rx = /a+b*/;
~~~~

Az egyenlőségjel jobboldalán tetszőleges, a megfeleő típust visszaadó kifejezés, így függvényhívás is szerepelhet. `list` esetén a szögletes zárójelek közt csak `string` literálok sorolhatók fel. 

`regex` típus esetén, ahogy a példában is látható, a reguláris kifejezést `/` jelek közé kell zárni, ahogy JavaScriptben.

Egyszerre csak egy változó deklarálására van lehetőség, a következő nem megengedett:

~~~~
  string a, b, c;
~~~~

### Kezdőérték

Ha egy típust nem inicializálunk, akkor a változó kezdőértéke

  * `string` esetén az üres `string` azaz `""` lesz,
  * `list` esetén az üres lista, azaz `[]` lesz,
  * `regex` esetén az üres kiefejezés, azaz `//` lesz.

## Értékadás

Változóknak új értéket adhatunk, ha az `=` baloldalán adjuk meg őket:

~~~~
  string str;

  str = "abc";

  str = "acde";
~~~~ 

Természetesen függvényhívást is használhatunk. Lehetőség van arra is, hogy egyidőben több változót is azonos értékre állítsunk be, a következő módon:

~~~~
  string a;
  string b;

  a = b = "Hello!";
~~~~

Ilyenkor a legjobboldalibb kifejezés kerül először kiértékelésre, majd a kapott értéket jobbról balra haladva elhelyezzük a felsorolt változókban. 

## For ciklus

Egy speciális `for` ciklus használható a nyelvben, mely egy `list` típusú változó elemein képes végigiterálni. Ehhez meg kell adnunk egy ciklusváltozót, valamint egy `list` típusú változót:

~~~~
  for (e : lst) {
    // ciklusmag
  }
~~~~

Természetesen, ha üres a lista, a ciklusmag egyszer sem hajtódik végre. Egyéb esetben a ciklusváltozó (a példában `e`) sorban felveszi a lista elemeinek értékét, és rendelkezésre áll a ciklusmagban.

Amennyiben a ciklusmag egyetlen utasításból áll, elhagyhatjuk a kapcsos zárójeleket:

~~~~
  for (e : lst)
    print(e);
~~~~

## Függvények

Függvények általánosan a következőképpen tudunk deklarálni:

~~~~
  function visszatérési_érték azonosító([paraméterek])
    törzs
~~~~

Mindeképpen a `function` kulcsszóval kell tehát kezdenünk a függvények deklarációját. Ezt követi a visszatérési érték. Olyan függvények esetén, melyek nem adnak vissza értéket, a `void` értéket kell itt megadnunk.

Az azonosító lesz a függvény neve, amivel majd meg tudjuk hívni. Erre ugyanazok a szabályok vonatkoznak, mint a változók azonosítóira. 

A függvény formális paraméterlistáját (amennyiben elfogad paramétereket) zárójelek között kell megadnunk. Ebben a formális paramétereket vesszővel elválasztva lehet felsorolni:

~~~~
  function void f(string a, list b, regex c) {
    // törzs
  }
~~~~

A függvény törzsét kapcsos zárójelek között adhatjuk meg, melyek azonban elhagyhatóak, ha a függvény mindössze egyetlen utasításból áll. Például:

~~~~
  function void prnt(string str)
    print(str);
~~~~ 

Ha a függvény visszatérési értéke nem `void`, akkor a `return` segítségével térhetünk vissza a függvényből valamilyen értékkel:

~~~~
  function string id(string str) {
    return str;
  }
~~~~

A visszaadott érték típusának meg kell egyeznie a függvény szignatúrájában meghatározott típussal.

A függvényben lehetséges összes végrehajtási ágon szerepelnie kell egy `return` utasításnak.