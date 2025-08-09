import React from "react";
import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";

export default function EmailList({ emails, columns }) {
    if (!emails || emails.length === 0) {
        return <p>Veri bulunamadı.</p>;
    }

    return (
        <DataTable value={emails} paginator rows={10} responsiveLayout="scroll">
            {columns.map((col) => (
                <Column key={col} field={col} header={col} sortable />
            ))}
        </DataTable>
    );
}
