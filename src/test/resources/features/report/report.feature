Feature: Reporting

  Background:
    Given the following date an time: 01-10-2023 10:00
    And an anonymous charge session started at "01-09-2023 12:45" on 120.556 and ended at "01-09-2023 18:03" on 141.369

  Scenario: an e-mail has been sent stating that the generation of a report is not possible
    When the monthly cronjob is triggered
    Then an e-mail is sent to "to@foo.bar" with subject "Declaratie genereren niet mogelijk"
    And it contains the message:
    """
    Genereren van een declaratie voor de maand september is niet gelukt.
    """

  Scenario: an e-mail has been sent containing the declaration of the last month
    Given a registered charge session started at "02-09-2023 22:31" on 141.369 and ended at "03-09-2023 03:28" on 157.836 with odo-meter 58720
    When the monthly cronjob is triggered
    Then an e-mail with attachment is sent to "to@foo.bar" with subject "Declaratie gegenereerd"
    And it contains the message:
    """
    Declaratie voor de maand september gegenereerd.
    """
    And a file with the name "declaratie-2023-09.pdf"
    And the file contains the amount of "€ 3,62" for the month "september" on license plate "AB-123-C"

  Scenario: the report only contains charge sessions from the last month
    Given a registered charge session started at "30-08-2023 23:26" on 97.748 and ended at "31-08-2023 01:22" on 120.556 with odo-meter 58401
    And a registered charge session started at "02-09-2023 22:31" on 141.369 and ended at "03-09-2023 03:28" on 157.836 with odo-meter 58720
    When the monthly cronjob is triggered
    Then an e-mail with attachment is sent to "to@foo.bar" with subject "Declaratie gegenereerd"
    And it contains the message:
    """
    Declaratie voor de maand september gegenereerd.
    """
    And a file with the name "declaratie-2023-09.pdf"
    And the file contains the amount of "€ 3,62" for the month "september" on license plate "AB-123-C"

  Scenario: the report contains the total amount of all registered charge sessions
    Given a registered charge session started at "02-09-2023 23:26" on 141.369 and ended at "03-09-2023 01:22" on 157.836 with odo-meter 58401
    And a registered charge session started at "02-09-2023 22:31" on 157.836 and ended at "03-09-2023 03:28" on 199.257 with odo-meter 58720
    And a registered charge session started at "04-09-2023 18:42" on 199.257 and ended at "04-09-2023 23:49" on 237.992 with odo-meter 59093
    And a anonymous charge session started at "05-09-2023 19:32" on 237.992 and ended at "05-09-2023 23:29" on 245.741
    And a registered charge session started at "07-09-2023 20:12" on 245.741 and ended at "08-09-2023 01:13" on 279.648 with odo-meter 59412
    When the monthly cronjob is triggered
    Then an e-mail with attachment is sent to "to@foo.bar" with subject "Declaratie gegenereerd"
    And it contains the message:
    """
    Declaratie voor de maand september gegenereerd.
    """
    And a file with the name "declaratie-2023-09.pdf"
    And the file contains the amount of "€ 28,72" for the month "september" on license plate "AB-123-C"