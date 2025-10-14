import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
export function Pagination({ page, totalPages, onChange }) {
    if (totalPages <= 1) {
        return null;
    }
    return (_jsxs("div", { className: "pagination", children: [_jsx("button", { disabled: page === 0, onClick: () => onChange(page - 1), children: "Anterior" }), _jsxs("span", { children: ["P\u00E1gina ", page + 1, " de ", totalPages] }), _jsx("button", { disabled: page + 1 >= totalPages, onClick: () => onChange(page + 1), children: "Siguiente" })] }));
}
