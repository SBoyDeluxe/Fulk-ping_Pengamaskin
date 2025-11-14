package org.example.moneymachine;

import org.example.moneymachine.banks.*;
import org.example.moneymachine.banks.implementations.*;
import org.example.moneymachine.controller.UI.*;
import org.example.moneymachine.repository.*;
import org.example.moneymachine.service.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.*;
import org.springframework.context.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.*;

//@EnableJpaRepositories("org.example.moneymachine.*")
//@ComponentScan(basePackages = { "org.example.moneymachine.*" })
//@EntityScan("org.example.moneymachine.*")
@org.springframework.boot.autoconfigure.SpringBootApplication
public class SpringBootApplication {






    /**
     * <details>
     *     <summary>Specifications</summary>
     *     <header>
     *        <h2> 1 Inlämningsuppgift - Pengamaskinen i Fulköping </h2>
     *     </header>
     *     <main>
     *         <section>
     *             <h4>Introduktion</h4>
     *             <p>
     *              Din uppgift är att bygga en uttagsautomat till Fulköpings Bank. Banken kommer att vidareutveckla och
     *              underhålla kodbasen i många år framöver och har därför bestämt att utvecklingen skall ske med testdriven
     *              utvecklning (TDD) och att samtliga användarfall skall vara täckta med enhetstester. I automaten skall du
     *              stoppa in ditt bankkort och verfiera att du är du med en PIN-kod. Därefter skall du kunna se saldo, ta ut
     *              pengar men även sätta in pengar på ditt konto.
     *              Målet är att du skall visa:
     *              <ul>
     *                  <li> Hur man utvecklar en applikation med hjälp av TDD.</li>
     *                  <li> Hur man skapar enhetstester.</li>
     *                  <li> Hur man kan mocka funktionallitet.</li>
     *              </ul>
     *              </p>
     *
     *         </section>
     *         <section>
     *             <h4>Utförande:</h4>
     *             <p>
     *           Banken gillar dig, men du är inte betrodd att ansluta direkt till bankens apier. Därför kommer du att vara
     *           tvungen att utveckla denna applikationen med hjälp av en mock av bankapiet. Till uppgiften får du ett
     *           enkelt skal med kod (se inlämning nedan) - här finns ett påbörjat inferface för bankens API, men eftersom
     *           detta projekt är den första konsumenten av API:et kommer banken att implementera de metoder du
     *           behöver (dvs du kan, och behöver, lägga till och ändra metoder i interfacet).
     *           </p>
     *           <ol>
     *               <h6>Användarfall</h6>
     *               <li>När användaren matar in sitt kort används kortets serienummer för att identifera användaren.</li>
     *               <li>
     *                   Kontrollera om kortet är låst
     *                   <ol>
     *                       <li>Kortet är inte låst :
     *                          <br>
     *                          <ul>
     *                            <li>  Användaren notifieras om antalet återstående försök :
     *                       <ol>
     *                           <li>Användaren anger sin PIN-kod. Pin-koden verifieras via bankens api.
     *                           <ul>
     *                               <li>Om pin-koden stämmer blir användaren inloggad.</li>
     *                           <li>Om pin-koden är fel, hämta antalet misslyckade försök från banken. Spara
     *                               antalet försök +1 till banken via dess api.</li>
     *                           <li>Om användaren har försökt 3 gånger med fel pin-kod, låses kortet via bankens api.</li>
     *                          </ul>
     *                       </ol>
     *                       </li>
     *                          </ul>
     *                       </li>
     *                       <li>Kortet är låst:
     *                              <ol>
     *                                  <li>Meddela användaren</li>
     *                                  <li>Avbryt inloggning</li>
     *                              </ol>
     *                       </li>
     *                   </ol>
     *           </ol>
     *           <br>
     *           <hr>
     *           <article>
     *               <h6>Inloggad användare :</h6>
     *               <ol>
     *                   <li>Kontrollera saldo: Hämta saldo från banken och visa det för användaren.</li>
     *                   <li>
     *                          Sätt in pengar: Användaren anger ett belopp som hen vill sätta in. Säkerställ att bankens
     *                           insättningsfunktion anropas på rätt sätt.
     *                   </li>
     *                   <li>
     *                       Ta ut pengar :
     *                       <ol>
     *                           <li>Användaren anger ett belopp som hen vill ta ut.</li>
     *                           <li>
     *                               Kontrollera uttag :
     *                               <ul>
     *                                   <li>Uttag möjligt :
     *                                          <li>Behandla uttaget och säkerställ att bankens uttagsfunktion anropas på
     *                                                 korrekt sätt
     *                                          </li>
     *                                  </li>
     *                                  <li>
     *                                      Uttag ej möjligt :
     *                                      <ol>
     *                                          <li>
     *                                              Visa felmeddelande om att saldot är för lågt
     *                                          </li>
     *                                      </ol>
     *                                  </li>
     *                               </ul>
     *                           </li>
     *                       </ol>
     *                   </li>
     *                   <li>Logga ut :
     *                      <ul>
     *                          <li>Användare avslutar inloggning och kortet matas ut</li>
     *                      </ul>
     *                   </li>
     *               </ol>
     *           </article>
     *         </section>
     *         <br>
     *         <hr>
     *         <section>
     *             <h6>Bankomat </h6>
     *
     *             <ul>
     *                 <li>Ska kunna bekräfta vilken bank den är ansluten till</li>
     *             </ul>
     *         </section>
     *         <br>
     *         <hr>
     *         <section>
     *             <h6>Bank:</h6>
     *             <ul>
     *                 <li>Ska innehålla statisk metod för att visa bankens namn</li>
     *             </ul>
     *         </section>
     *         <br>
     *         <hr>
     *         <section>
     *             <h6>Test</h6>
     *             <ul>
     *                 <li>Använd Mockito för att mocka bankens funktioner</li>
     *                 <li>
     *                    Använd verify för att säkerställa att korrekt metoder i den mockade banken anropas vid speciella
     *                      operationer (t.ex. insättning och uttag).
     *                 </li>
     *                 <li>
     *                  Mocka den statiska funktionen som bekräftar bankens identitet för att säkerställa korrekt
     *                  bankanslutning.
     *                 </li>
     *                 <li>
     *                  Din kod skall ha minst 80% testtäckning (ATM-klassen).
     *                 </li>
     *                 <li>
     *                  Koden skall vara uppdelad i två paket; main och test.
     *                 </li>
     *                 <li>
     *                  Använd minst anoteringarna @DisplayName och @Test på alla testmetoder, använd gärna fler
     *                 </li>
     *             </ul>
     *         </section>
     *         <br>
     *         <hr>
     *     </main>
     *  <p>
     *
     *  </p>
     * </details>
     * <hr>
     *   <details>
     *       <summary>Implementation :</summary>
     *       <header>
     *           <h5>Overview :</h5>
     *           <br>
     *           <p>
     *               This api follows the Service/Controller/Repository-pattern set out by the Spring boot-framework.
     *               <br>
     *               We assume that a given bank provides a database-solution that can be implemented using {@linkplain JpaRepository }.
     *               <br>
     *               The specifications of the API, namely :
     *               <ol>
     *                   <li>Login to user-account via userId (card-number) and pin /card-pin-number)/li>
     *                   <li>Logging and reset of failed login-attempts for a specific user
     *                             <ul>
     *                                <li>Exposing if a specific user account is locked due to too many login-attempts</li>
     *                                <li>Exposing number of login-attempts</li>
     *                            </ul>
     *                   </li>
     *                   <li>Management of user-account
     *                          <ul>
     *                              <li>Depositing money</li>
     *                              <li>Withdrawing money</li>
     *                          </ul>
     *                   </li>
     *
     *               </ol>
     *               <br>
     *
     *           </p>
     *       </header>
     *       <table>
     *           <caption>Description of API-implementation</caption>
     *           <tr>
     *               <th></th>
     *               <th>{@link APIBankInterface}</th>
     *               <th>{@link BankEntityRepository}</th>
     *               <th>{@link ATM}</th>
     *           </tr>
     *           <tr>
     *               <th>Use :</th>
     *               <td></td>
     *           </tr>
     *       </table>
     *   </details>
     * <hr>
     *
     *
     * @param args
     */
    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(SpringBootApplication.class, args);

        ATMConfig atmConfig = new ATMConfig(applicationContext.getBean(MockBank.class), applicationContext.getBean(MasterCardBank.class));
        ATM atm = atmConfig.ATM();
        UserInterface userInterface = new UserInterface();

        userInterface.startMenu(atm.getConnectedBanks());
        try {
            System.out.println("Waiting for card...");
            double randomTimeToCardInsertion = Math.random() * 2000;
            Thread.sleep((long) randomTimeToCardInsertion);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);

        }
        System.out.println("Card inserted time");

        //List<String> validUserIds = List.of();

    }

}
