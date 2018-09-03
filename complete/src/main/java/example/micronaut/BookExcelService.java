package example.micronaut;

import java.util.List;
import io.micronaut.http.server.types.files.AttachedFile;

public interface BookExcelService {
    static final String SHEET_NAME = "Books";
    static final String HEADER_ISBN = "Isbn";
    static final String HEADER_NAME = "Name";
    static final String HEADER_EXCEL_FILENAME = "books.xlsx";

    AttachedFile excelFileFromBooks(List<Book> bookList); // <1>
}
