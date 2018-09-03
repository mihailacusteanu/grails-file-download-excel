package example.micronaut

import builders.dsl.spreadsheet.query.api.SpreadsheetCriteria
import builders.dsl.spreadsheet.query.api.SpreadsheetCriteriaResult
import builders.dsl.spreadsheet.query.poi.PoiSpreadsheetCriteria
import geb.spock.GebSpec
import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.Environment
import io.micronaut.runtime.server.EmbeddedServer
import spock.lang.AutoCleanup
import spock.lang.IgnoreIf
import spock.lang.Shared
import spock.util.concurrent.PollingConditions

class DownloadExcelSpec extends GebSpec {

    @Shared
    @AutoCleanup
    EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer, [:], Environment.TEST)

    @IgnoreIf({ !sys['download.folder'] || sys['geb.env'] != 'chrome' })
    def "books can be downloaded as an excel file"() {
        given:
        PollingConditions conditions = new PollingConditions(timeout: 5)
        browser.baseUrl = "http://localhost:${embeddedServer.port}"

        when:
        browser.to HomePage

        then:
        browser.at HomePage

        when: 'clicking excel button'
        String expectedPath = System.getProperty('download.folder') + "/" + BookExcelService.HEADER_EXCEL_FILENAME
        File outputFile = new File(expectedPath)
        browser.page(HomePage).downloadExcel()

        then: 'an excel file is downloaded'
        conditions.eventually { outputFile.exists() }

        when: 'if we search for a row with a particular value (Building Microservices)'
        SpreadsheetCriteria query = PoiSpreadsheetCriteria.FACTORY.forFile(outputFile)
        SpreadsheetCriteriaResult result = query.query {
            sheet(BookExcelService.SHEET_NAME) {
                row {
                    cell {
                        value 'Building Microservices'
                    }
                }
            }
        }

        then: 'a row is found'
        result.cells.size() == 1

        cleanup:
        new File(expectedPath).delete()
    }
}
