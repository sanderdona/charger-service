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

  Scenario: an e-mail has been sent containing the declaration of the previous month
    And an registered charge session started at "02-09-2023 22:31" on 141.369 and ended at "03-09-2023 03:28" on 157.836
    When the monthly cronjob is triggered
    Then an e-mail with attachment is sent to "to@foo.bar" with subject "Declaratie gegenereerd"
    And it contains the message:
    """
    Declaratie voor de maand september gegenereerd.
    """
    And a file with the name "declaratie-2023-09.pdf"
    And the file contains the amount of "€ 3,62" for the month "september" on license plate "AB-123-C"