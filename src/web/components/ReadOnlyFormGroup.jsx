import React from 'react';
import styled, { css } from 'styled-components';

import { Col, Row, HelpBlock } from 'react-bootstrap';

const ValueCol = styled(Col)`
  padding-top: 7px;
`;

const LabelCol = styled(ValueCol)(({ theme }) => css`
  font-weight: bold;
  @media (min-width: ${theme.breakpoints.min.md}) {
    text-align: right;
  }
`);

const readableValue = (value) => {
  if (value) {
    return value;
  }

  return '-';
};

const ReadOnlyFormGroup = ({ label, value, help, className }) => (
  <Row className={className}>
    <LabelCol sm={3}>
      {label}
    </LabelCol>
    <ValueCol sm={9}>
      {readableValue(value)}
      {help && <HelpBlock>{help}</HelpBlock>}
    </ValueCol>
  </Row>
);

ReadOnlyFormGroup.defaultProps = {
  help: undefined,
  className: undefined,
};

export default ReadOnlyFormGroup;
