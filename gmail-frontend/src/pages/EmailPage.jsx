import React, { useMemo, useRef, useState } from "react";
import { fetchEmailsWithFeign, fetchEmailsWithGrpc, fetchGraphqlEmails } from "../api/api";
import EmailList from "../components/EmailList";
import { Dropdown } from "primereact/dropdown";
import { MultiSelect } from "primereact/multiselect";
import { Button } from "primereact/button";
import { Card } from "primereact/card";
import { Toast } from "primereact/toast";

const GRAPHQL_FIELDS = [
    "emailId", "subject", "sender", "snippet", "receivedAt",
    "labelIds", "hasAttachment", "sizeEstimate"
];



export default function EmailPage() {
    const [protocol, setProtocol] = useState("FEIGN");
    const [selectedCategories, setSelectedCategories] = useState([
        { name: "emailId", key: "emailId" },
        { name: "subject", key: "subject" }
    ]);
    const [emails, setEmails] = useState([]);
    const [loading, setLoading] = useState(false);
    const toast = useRef(null);

    const isGraphQL = useMemo(() => protocol === "GRAPHQL", [protocol]);
    const selectedFieldList = useMemo(
        () => selectedCategories.map((c) => c.key),
        [selectedCategories]
    );

    const protocolOptions = [
        { label: "Feign", value: "FEIGN" },
        { label: "gRPC", value: "GRPC" },
        { label: "GraphQL", value: "GRAPHQL" }
    ];

    const categories = GRAPHQL_FIELDS.map((f) => ({ name: f, key: f }));

    const handleFetch = async () => {
        setLoading(true);
        setEmails([]);
        try {
            let res;
            if (isGraphQL) {
                if (!selectedCategories.length) {
                    toast.current.show({ severity: "warn", summary: "Uyarı", detail: "En az bir alan seçmelisiniz." });
                    return;
                }
                res = await fetchGraphqlEmails(selectedFieldList.join(","));
            } else if (protocol === "FEIGN") {
                res = await fetchEmailsWithFeign();
            } else if (protocol === "GRPC") {
                res = await fetchEmailsWithGrpc();
            }
            setEmails(res);
        } catch (err) {
            toast.current.show({ severity: "error", summary: "Hata", detail: err.message || "Bir hata oluştu." });
        } finally {
            setLoading(false);
        }
    };

    return (
        <Card title="E-posta Görüntüleyici" className="p-4">
            <Toast ref={toast} />

            {/* Protokol Seçimi */}
            <div className="p-field mb-3">
                <label htmlFor="protocol" className="font-bold mb-2">Protokol Seç</label>
                <Dropdown
                    id="protocol"
                    value={protocol}
                    options={protocolOptions}
                    onChange={(e) => setProtocol(e.value)}
                    placeholder="Protokol seçiniz"
                    className="w-full md:w-20rem"
                />
            </div>

            {/* GraphQL Alan Seçimi */}
            {isGraphQL && (
                <div className="p-field mb-3">
                    <label htmlFor="graphqlFields" className="font-bold mb-2">GraphQL Alanları</label>
                    <MultiSelect
                        id="graphqlFields"
                        value={selectedCategories}
                        options={categories}
                        onChange={(e) => setSelectedCategories(e.value)}
                        optionLabel="name"
                        placeholder="Alan seçiniz"
                        display="chip"
                        filter
                        className="w-full md:w-30rem"
                    />
                </div>
            )}

            {/* Buton */}
            <div className="p-field mb-3">
                <Button
                    label={loading ? "Yükleniyor..." : "Veriyi Getir"}
                    icon="pi pi-download"
                    onClick={handleFetch}
                    loading={loading}
                    disabled={loading || (isGraphQL && !selectedCategories.length)}
                />
            </div>

            {/* Email Listesi */}
            <EmailList
                emails={emails}
                columns={isGraphQL ? selectedFieldList : ["emailId", "subject", "sender"]}
            />
        </Card>
    );
}
