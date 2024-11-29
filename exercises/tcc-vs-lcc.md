# TCC *vs* LCC

Explain under which circumstances *Tight Class Cohesion* (TCC) and *Loose Class Cohesion* (LCC) metrics produce the same value for a given Java class. Build an example of such as class and include the code below or find one example in an open-source project from Github and include the link to the class below. Could LCC be lower than TCC for any given class? Explain.

A refresher on TCC and LCC is available in the [course notes](https://oscarlvp.github.io/vandv-classes/#cohesion-graph).

## Answer
Pour un classe données les métriques de TCC et LCC peuvent être les même si toutes les méthodes ont des varriables en commun entre elles, dans ce cas la,  la veleur de TCC et de LCC est égal à x! où X est le nombre de méthodes, ou si aucunes des méthodes n'ont de varriable communes dans ce cas les valeurs de TCC et LCC sont égale à 0.

Exemple : 
    public class Math {

        private int a;
        private int b;    
        
        public int addition() {
            return a + b;
        }

        public int multiplication() {
            return a * b;
        }

        public int sommeCarre() {
            return (a * a) + (b * b);
        }
    }
Ici tout les classes ont des instances de varriable en commun : TCC = 6/6 et LCC = 6/6

LCC ne pourra jamais être plus bas que TCC car comme LCC prend en compte les pairs connecté meme indirectement et TCC que les connection directe, TCC est un sous ensemble de LCC et ne peut pas être plus grands
