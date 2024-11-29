# Using PMD

Pick a Java project from Github (see the [instructions](../sujet.md) for suggestions). Run PMD on its source code using any ruleset (see the [pmd install instruction](./pmd-help.md)). Describe below an issue found by PMD that you think should be solved (true positive) and include below the changes you would add to the source code. Describe below an issue found by PMD that is not worth solving (false positive). Explain why you would not solve this issue.

## Answer

PMD nous remonte de nombreux cas de parenthèses inutiles de type : 

    commons-math/commons-math-core/src/main/java/org/apache/commons/math4/core/jdkmath/AccurateMath.java:396:	UselessParentheses:	Useless parentheses.

Ce problème n'en est pas un et les parenthèses permettent généralement d'améliorer la lecture du programme, sa compréhension et sa maintenance. Il s'agirait d'un problème remonté que nous ne corrigerons pas (faux positif).

PMD nous remonte aussi ce problème : 

    commons-math/commons-math-legacy/src/main/java/org/apache/commons/math4/legacy/ml/clustering/DBSCANClusterer.java:204:	CompareObjectsWithEquals:	Use equals() to compare object references.

Il s'agit d'une comparaison dans l'algorithme des k-neighbors pour vérifier que l'on ne traite pas un point A comme son propre voisin. Cette comparaison est faites telle qui suit avec l'opérateur !=. 

    point != neighbor

Puisque cette fonction prend en paramètre un objet de type T, la bonne pratique pour éviter les bugs de comparaison est plutôt d'utiliser la fonction ```equals``` afin de pouvoir comparer les objets correctement. La correction serait donc : 

    !point.equals(neigbhor)
