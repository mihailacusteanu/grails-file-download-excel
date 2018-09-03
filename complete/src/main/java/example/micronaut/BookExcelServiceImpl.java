package example.micronaut;

import builders.dsl.spreadsheet.builder.poi.PoiSpreadsheetBuilder;
import io.micronaut.http.server.types.files.AttachedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Stream;

@Singleton // <1>
public class BookExcelServiceImpl implements BookExcelService {

    private static final Logger LOG = LoggerFactory.getLogger(BookExcelServiceImpl.class);

    @Override
    public AttachedFile excelFileFromBooks(List<Book> bookList) {
        File file = new File(HEADER_EXCEL_FILENAME);
        try {
            PoiSpreadsheetBuilder.create(file).build(w -> {
                w.apply(BookExcelStylesheet.class);
                w.sheet(SHEET_NAME, s -> {
                    s.row(r -> Stream.of(HEADER_ISBN, HEADER_NAME)
                            .forEach(header -> r.cell(cd -> {
                                    cd.value(header);
                                    cd.style(BookExcelStylesheet.STYLE_HEADER);
                                })
                            ));
                    bookList.stream()
                            .forEach( book -> s.row(r -> {
                                r.cell(book.getIsbn());
                                r.cell(book.getName());
                            }));
                });
            });
        } catch (FileNotFoundException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error("File not found exception raised when generating excel file");
            }
        }

        return new AttachedFile(file, HEADER_EXCEL_FILENAME);
    }
}