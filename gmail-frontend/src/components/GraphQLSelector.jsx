import React from "react";
import { MultiSelect } from "primereact/multiselect";

export default function GraphQLFieldSelector({ fields, selected, onChange }) {
    return (
        <div>
            <label style={{ fontWeight: 600, marginBottom: 8, display: "block" }}>
                GraphQL Alanları
            </label>
            <MultiSelect
                value={selected}
                options={fields}
                onChange={(e) => onChange(e.value)}
                optionLabel="name"
                placeholder="Alan seçin"
                display="chip" // seçilenleri chip olarak gösterir
                filter // arama çubuğu ekler
                style={{ width: "100%" }}
            />
        </div>
    );
}
